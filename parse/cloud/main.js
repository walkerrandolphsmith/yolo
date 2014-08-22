var Mailgun = require('mailgun');
Mailgun.initialize('yolosafely.com', 'key-52htx6eslf80d390vqke713spzbzr3j3');

// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("sendEmail", function(request, response) {

var email = request.params.email;
var message = request.params.message;

    Mailgun.sendEmail({
  to: email,
  from: "yolo@yolosafely.com",
  subject: "Yolo",
  text: message
}, {
  success: function(httpResponse) {
    console.log(httpResponse);
    response.success("Email sent!");
  },
  error: function(httpResponse) {
    console.error(httpResponse);
    response.error("Uh oh, something went wrong");
  }
});


  response.success("Email sent to : " + email);
});
