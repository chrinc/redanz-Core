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
          Thank you for your registration!
        </p>
        <p>
          We will check your entries and inform you as soon as possible if we can offer you a spot at this year's Workshop!
        </p>
        <p>
          You can find the details of your submission in your <a href=${link} >redanz account</a>
        </p>

        <p>See you soon</p>
        Best Regards </br>
        Team redAnz
      </div>
    </div>
  </body>
</html>