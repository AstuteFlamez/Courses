"""Grant read-only access to a student's feedback repo for a partner."""

from __future__ import annotations

import importlib.util
import types
from types import ModuleType
import sys
from pathlib import Path

SCRIPTS_ROOT = Path("/home/cbford/Projects/TA/sum_2026_cs400/scripts")
CS400ADMIN_ROOT = SCRIPTS_ROOT / "cs400admin"


def load_module(module_name: str, file_path: Path) -> ModuleType:
    spec = importlib.util.spec_from_file_location(module_name, file_path)
    if spec is None or spec.loader is None:
        raise RuntimeError(f"Unable to load {module_name} from {file_path}")
    module = importlib.util.module_from_spec(spec)
    sys.modules[module_name] = module
    spec.loader.exec_module(module)
    return module


if "cs400admin" not in sys.modules:
    package = types.ModuleType("cs400admin")
    package.__path__ = [str(CS400ADMIN_ROOT)]
    sys.modules["cs400admin"] = package

config_module = load_module("cs400admin.config", CS400ADMIN_ROOT / "config.py")
gitlab_module = load_module("cs400admin.gitlab", CS400ADMIN_ROOT / "gitlab.py")

load_config = config_module.load_config
GitlabClient = gitlab_module.GitlabClient
GitlabError = gitlab_module.GitlabError
encode_path = gitlab_module.encode_path


def main(argv: list[str] | None = None) -> int:
    args = list(argv if argv is not None else sys.argv[1:])
    if len(args) != 2:
        print("Usage: grant_feedback_access.py <netid> <partner>", file=sys.stderr)
        return 1

    netid = args[0].strip().lower()
    partner = args[1].strip().lower()

    cfg = load_config(SCRIPTS_ROOT / "config.toml")
    gl = GitlabClient.from_config(cfg.gitlab)

    group_path = f"{cfg.gitlab.course_group_path}students/{partner}"
    projects = gl.paged(f"groups/{encode_path(group_path)}/projects", per_page=100)
    project = next((item for item in projects if item.get("name") == "P206.RoleCode"), None)
    if project is None:
        print(f"WARNING: failed to find P206.RoleCode repo for {partner}")
        return 0

    users = gl.get("users", username=netid) or []
    if len(users) != 1:
        print(f"WARNING: expected exactly one GitLab user for {netid}, found {len(users)}")
        return 0

    user_id = users[0]["id"]
    project_id = project["id"]
    member = gl.try_get(f"projects/{project_id}/members/{user_id}")
    if member is not None and member.get("access_level") == 20:
        return 0

    try:
        if member is not None:
            response = gl.put(f"projects/{project_id}/members/{user_id}", json={"access_level": 20})
        else:
            response = gl.post(f"projects/{project_id}/members", json={"user_id": user_id, "access_level": 20})
    except GitlabError as error:
        print(f"WARNING: failed to set read-only permissions for {netid} on {partner} p102 repo: {error}")
        return 0

    if response.get("access_level") != 20:
        print(f"WARNING: failed to set read-only permissions for {netid} on {partner} p102 repo: {response}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
