#!/usr/bin/env python3
"""Black-box contract smoke test. Tests only features already present in master."""
import json
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

BASE = "http://localhost:9080/hlcasestudy/rest/useraccounts"

def call(method, path="", payload=None):
    body = json.dumps(payload).encode() if payload is not None else None
    headers = {"Content-Type": "application/json"} if body is not None else {}
    req = Request(BASE + path, data=body, headers=headers, method=method)
    try:
        with urlopen(req, timeout=10) as response:
            raw = response.read()
            return response.status, json.loads(raw) if raw else None
    except HTTPError as error:
        return error.code, error.read().decode(errors="replace")

def equal(actual, expected, where):
    if actual != expected:
        raise AssertionError(f"{where}: expected {expected!r}, got {actual!r}")

def main():
    for i in range(90):
        try:
            status, users = call("GET")
            if status == 200 and isinstance(users, list):
                break
            if i % 10 == 0:
                print("waiting for app:", status, str(users)[:300], flush=True)
        except (URLError, TimeoutError) as e:
            if i % 10 == 0:
                print("waiting for server:", e, flush=True)
        time.sleep(2)
    else:
        raise RuntimeError("API failed to become ready")

    equal(len(users), 3, "original three seeded users")
    equal([u["username"] for u in users], ["Homer", "Bart", "Marge"], "seed order")

    status, selected = call("GET", "?pageSize=2&page=2")
    equal(status, 200, "paginated HTTP code")
    equal([u["username"] for u in selected], ["Marge"], "1-based pagination")

    status, homer = call("GET", "/1")
    equal(status, 200, "GET /1")
    equal(homer["username"], "Homer", "GET by id")

    status, _ = call("POST", payload={"username": "Lisa", "age": 8, "gender": "FEMALE"})
    equal(status, 201, "POST")

    status, users = call("GET")
    equal(status, 200, "GET after POST")
    lisa = next(u for u in users if u["username"] == "Lisa")
    assert lisa.get("creationTime") is not None, "original @PrePersist"

    # Intentionally send only ID and age: original PUT does a partial update.
    status, _ = call("PUT", payload={"id": lisa["id"], "age": 9})
    equal(status, 204, "partial PUT")

    status, updated = call("GET", f"/{lisa['id']}")
    equal(status, 200, "GET after PUT")
    equal(updated["age"], 9, "age mutation")
    equal(updated["gender"], "FEMALE", "preserved gender")

    status, _ = call("DELETE", f"/{lisa['id']}")
    equal(status, 204, "DELETE")
    status, _ = call("GET", f"/{lisa['id']}")
    equal(status, 404, "GET deleted user")

    print("PASS: same original CRUD, seed, pagination and response statuses")

if __name__ == "__main__":
    try:
        main()
    except Exception:
        import traceback
        traceback.print_exc()
        sys.exit(1)
