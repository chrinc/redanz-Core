<!DOCTYPE html>
<html>
  <head>
    <base target="_top">
      <?!= include("css.html") ?>
  </head>
  <body>
    <div style="width:730px;background:#ececec;text-align:left;font-family: Arial;">
      <img src="https://stirit.ch/wp-content/uploads/2020/05/stirit20_WEB_header_1a.png" style="margin:15px" width="700"><br/>
      <div style="padding:15px;">
        <h3>Hi ${firstName}</h3>
        <p>
          Hurray - we've got a spot for you at the this year's Workshop.
          As soon as we receive your payment, we will confirm your participation.
        </p>
        <p>
          Please find the payment details in your  <a href=${link} >redanz account</a>
        </p>
        <p>See you soon</p>
        Best Regards </br>
        Team redAnz
      </div>
    </div>
  </body>
</html>