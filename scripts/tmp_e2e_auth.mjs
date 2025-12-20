import { readFile } from 'node:fs/promises';

const baseUrl = 'http://localhost:8081';

async function postJson(path, body) {
  const res = await fetch(baseUrl + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  const text = await res.text();
  return { status: res.status, text };
}

async function getJson(path, accessToken) {
  const res = await fetch(baseUrl + path, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });
  const text = await res.text();
  return { status: res.status, text };
}

const signupBody = JSON.parse(
  await readFile(new URL('./tmp_signup.json', import.meta.url), 'utf8')
);
const loginBody = JSON.parse(
  await readFile(new URL('./tmp_login.json', import.meta.url), 'utf8')
);

// 1) signup (이미 존재하면 실패할 수 있으니, 실패해도 다음 단계 진행)
const signup = await postJson('/api/v1/auth/signup', signupBody);
console.log('[signup]', signup.status);

// 2) login
const login = await postJson('/api/v1/auth/login', loginBody);
console.log('[login]', login.status);
if (login.status !== 200) {
  console.log(login.text);
  process.exit(1);
}
const loginJson = JSON.parse(login.text);
const accessToken = loginJson?.data?.accessToken;
if (!accessToken) {
  console.log('No accessToken in response');
  console.log(login.text);
  process.exit(1);
}

// 3) protected endpoint
const me = await getJson('/api/v1/users/me', accessToken);
console.log('[users/me]', me.status);
console.log(me.text);
