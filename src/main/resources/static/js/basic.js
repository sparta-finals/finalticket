let host = 'http://' + window.location.host;

$(document).ready(function () {
  const auth = getToken();
  console.log(auth);
  if(auth === '') {
    $('#login-true').show();
    $('#login-false').hide();
    // console.log('auth==null')
    // window.location.href = host + "/v1/users/login-page";
  } else {
    $('#login-true').show();
    $('#login-false').hide();
  }
})

function logout() {
  console.log("logout실행")
  $.ajax({
    type: "DELETE",
    url: `/v1/users/logout`,
    contentType: "application/json",
    async : false
  })
  .done(function (res, status, xhr) {

  })
  .fail(function (xhr, textStatus, errorThrown) {
    console.log('statusCode: ' + xhr.status);
    window.location.href = host + '/v1/users/login-page?error'
  });
}

function getToken() {
  let auth = Cookies.get('Authorization');
  if(auth === undefined) {
    return '';
  }
  return auth;
}