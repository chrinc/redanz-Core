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
          Thank you for registering. Please click on the below link to activate your account.
        </p>
        <blockquote style="Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px">
          <p style="Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c">
            <a href=${link} >Activate Now</a>
          </p>
        </blockquote>

          The Link will expire in 15 minutes.
        <p>See you soon</p>
        Best Regards </br>
        Team redAnz
      </div>
    </div>
  </body>
</html>