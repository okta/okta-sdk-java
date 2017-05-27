const util = require('../util');

util.describe('/api/v1/sessions/:id', (suite) => {
  const password = 'tlpWENT2m';
  suite.it('refreshes a session', req =>
    req.post({
      path: '/api/v1/users?activate=true',
      body: {
        profile: {
          firstName: 'Sessions',
          lastName: 'McJanky',
          email: 'mocktestexample-sessions@mocktestexample.com',
          login: 'mocktestexample-sessions@mocktestexample.com',
          mobilePhone: '555-415-1337',
        },
        credentials: {
          password: {
            value: password,
          },
        },
      },
    })
    .then(user =>
      req.post({
        path: '/api/v1/authn',
        body: {
          username: user.profile.login,
          password: password,
        },
      })
    )
    .then(authn =>
      req.post({
        path: '/api/v1/sessions',
        body: {
          sessionToken: authn.sessionToken,
        }
      })
    )
    .then(session =>
      req.post({
        path: `/api/v1/sessions/${session.id}/lifecycle/refresh`
      })
    )
  );
});
