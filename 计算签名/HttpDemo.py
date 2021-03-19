import base64
import hashlib
import hmac
import json
import random
import string
import time
from datetime import datetime, timedelta

import requests

# ali config
"""
api_link: https://developer.umeng.com/docs/143070/detail/144783
signature_link: https://developer.umeng.com/docs/143070/detail/144785
"""


def generate_code(length):
    key = string.digits + 'abcdef'
    str_list = random.sample(key + key, length)  # length >= len(key+key)
    result_str = ''.join(str_list)
    return result_str


# aliyun config
ali_appkey = 'xxxxxxxx'
ali_app_secret = 'xxxxxxxx'

# umeng config
host_url = 'https://verify5.market.alicloudapi.com'
path_url = '/api/v1/mobile/info'
umeng_app_key = 'xxxxxxxx'

token = 'xxxxxxxx'

body_data_str = json.dumps({'token': token})
content_md5_str = base64.b64encode(hashlib.md5(body_data_str.encode("UTF-8")).digest()).decode("UTF-8")

now_date_time = datetime.utcnow() + timedelta(hours=8)
timestamp = str(int(time.mktime(now_date_time.timetuple()) * 1000))

sign_headers_dict = {
    'X-Ca-Key': ali_appkey,
    'X-Ca-Stage': 'RELEASE',
    'X-Ca-Timestamp': timestamp,
    'X-Ca-Version': '1'
}

sign_headers_str = ''
for key in sorted(sign_headers_dict.keys()):
    value = sign_headers_dict[key]
    sign_headers_str = sign_headers_str + key + ':' + value + '\n'

sign_dict = {
    'HTTPMethod': 'POST',
    'Accept': 'application/json',
    'Content-MD5': content_md5_str,
    'Content-Type': 'application/json; charset=UTF-8',
    'Date': None,
    'Headers': sign_headers_str,
    'Url': path_url + '?' + 'appkey=' + umeng_app_key,
}

string_to_sign = ""
for key in sign_dict.keys():
    value = sign_dict[key]
    if key not in ['Headers', 'Url']:
        if value:
            string_to_sign = string_to_sign + value + "\n"
        else:
            string_to_sign = string_to_sign + "\n"
    else:
        string_to_sign = string_to_sign + value

key_bytes = ali_app_secret.encode('utf-8')
text_bytes = string_to_sign.encode('utf-8')
hash_bytes = hmac.new(key_bytes, text_bytes, hashlib.sha256).digest()
signature_str = base64.b64encode(hash_bytes).decode('utf-8')

nonce_str = generate_code(8) + '-' + generate_code(4) + '-' + generate_code(4) + '-' + generate_code(
    4) + '-' + generate_code(12)

headers_dict = {
    'Content-Type': 'application/json; charset=UTF-8',
    'Accept': 'application/json',
    'X-Ca-Version': '1',
    'X-Ca-Signature-Headers': 'X-Ca-Version,X-Ca-Stage,X-Ca-Key,X-Ca-Timestamp',
    'X-Ca-Stage': 'RELEASE',
    'X-Ca-Key': ali_appkey,
    'X-Ca-Timestamp': timestamp,
    'X-Ca-Nonce': nonce_str,
    'Content-MD5': content_md5_str,
    'X-Ca-Signature': signature_str
}

request_url = host_url + path_url + '?' + 'appkey=' + umeng_app_key
response = requests.post(url=request_url, data=body_data_str.encode("UTF-8"), headers=headers_dict)

print(response)
print(response.headers)
print(response.text)
