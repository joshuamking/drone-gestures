import json

MSG_PREFIX = 'CrazyflieDroneMsg|'


def jsonPrint(obj):
    print(MSG_PREFIX + json.dumps(obj.__dict__))
