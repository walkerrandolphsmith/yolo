var Mailgun = require('mailgun');
Mailgun.initialize('sandbox64940a76262d4b8d801bb0f6498a4dbd.mailgun.org', 'key-52htx6eslf80d390vqke713spzbzr3j3');

Parse.Cloud.define("sendEmail", function(request, response) {
    var email = request.params.email;
    Mailgun.sendEmail({
      to: email,
      from: "Mailgun@CloudCode.com",
      subject: "Hello from Cloud Code!",
      text: "Using Parse and Mailgun is great!"
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
    response.success("Hello world!");
});
