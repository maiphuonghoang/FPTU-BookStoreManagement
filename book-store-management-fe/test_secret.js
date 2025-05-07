import React from 'react'

import CodeBox from './index'

export default function Demo() {
  return (
    <div style={{width: '400px'}}>
      <CodeBox
        code={`
#Example python:
def get_source_issue(name:str, http_uri_clone:str, file_path:str, branch:str, personal_access_token:str):
    if not http_uri_clone or not file_path or not branch or not personal_access_token:
        logger.error("Invalid input")
        return JsonObject.new_obj()
    file_content_line = JsonObject.new_obj()
    """
    if name.startswith("GITHUB"):
        file_content_line = client_common.get_file_content_line_to_arr_github(http_uri_clone, file_path, branch, personal_access_token)
    elif name.startswith("GITLAB"):
        file_content_line = client_common.get_file_content_line_to_arr_gitlab(http_uri_clone, file_path, branch, personal_access_token)
"""

    if not file_content_line.is_none_or_empty():
        file_content_line.put("content", get_source_issue_from_scan_result(file_path, file_content_line.get("content").to_list())
)
    return file_content_line   
    pg_url=postgresql://eikh:EIKH%402024@103.xx.xx.xxx:5432/capstone_dev
db_name=capstone_dev
redis_host=localhost
redis_port=6379
secret_key="emiukhoahoc"
cache_token_hash="emiukhoahoc"
token_exp_minutes=120
cache_user_need_relogin=true
embedding_model="text-embedding-3-small"
openai_api_key= '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'
qdrant_url= 'https://1758eb50-6448-4294-8ddd-f4bbd9f8bf21.europe-west3-0.gcp.cloud.qdrant.io'
qdrant_api_key= 'aUPwm2EyQlsdd5q_gviovgdxXBfgLaUPwm2EyQltN9l6MMg'
sender_email="eikh2107@gmail.com"
sender_name="Tuyển dụng IERP"
sender_password="fakepassword"

            `}
        highlightIdxs={[20, 21, 22, 23]}
      />

      <CodeBox
        code={[
          {
            line: 166,
            content:
              '            placeholders = ", ".join(["%s"] * len(param_values))',
            is_error: false,
          },
          {
            line: 167,
            content:
              '            sql = f"CALL {procedure_name}({placeholders})"',
            is_error: false,
          },
          {
            line: 168,
            content: '            raw_cursor.execute(sql, param_values)',
            is_error: false,
          },
          {
            line: 169,
            content: '',
            is_error: false,
          },
          {
            line: 170,
            content:
              '            raw_cursor.execute(f"FETCH ALL IN {cursor_name}")',
            is_error: true,
          },
          {
            line: 171,
            content: '            rows = raw_cursor.fetchall()',
            is_error: false,
          },
          {
            line: 172,
            content: '',
            is_error: false,
          },
          {
            line: 173,
            content:
              '            columns = [col[0] for col in raw_cursor.description]',
            is_error: false,
          },
        ]}
      />
    </div>
  )
}