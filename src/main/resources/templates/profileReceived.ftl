<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="-webkit-text-size-adjust:none; -ms-text-size-adjust:none; margin:0; padding:0; font-family: Arial, sans-serif; font-size:16px; line-height:22px; color:#0b0c0c;">

<!-- HEADER IMAGE -->
<div style="width:100%; height:221px; overflow:hidden; display:flex; justify-content:center; align-items:center;">
    <img src="${headerLink}/email_header_01.png"
         alt="Header"
         style="height:221px; width:auto; min-width:100%; object-fit:cover;">
</div>

<!-- MAIN BODY -->
<div style="width:100%; background:#ececec; text-align:left; font-family: Arial, sans-serif; font-size:16px; line-height:22px;">
    <div style="padding:15px; color:#0b0c0c;">

        <!-- LANGUAGE CHANGE NOTICE -->
        <div style="text-align: center; margin-bottom:15px;">
            <em style="font-size:16px; line-height:22px;">${changeLanguage}</em>
        </div>

        <!-- GREETING -->
        <h3 style="font-size:20px; line-height:26px; margin:0 0 10px 0; font-weight:bold;">Hi ${firstName}</h3>

        <!-- MAIN CONTENT -->
        <p style="margin:0 0 15px 0; font-size:16px; line-height:22px;">
            ${base}
        </p>

        <!-- CALL TO ACTION BLOCKQUOTE -->
        <blockquote style="margin:0 0 20px 0; border-left:10px solid #b1b4b6; padding:15px 0 0.1px 15px; font-size:18px; line-height:25px;">
            <p style="margin:0 0 20px 0; font-size:18px; line-height:25px; color:#0b0c0c;">
                <a href="${registrationLink}" style="font-size:18px; color:#0000ee; text-decoration:none;">${activate_now}</a>
            </p>
        </blockquote>

        <!-- SPACING -->
        <br><br>

        <!-- FOOTER -->
        <table style="width:100%; height:150px; font-size:16px; line-height:22px;">
            <tr>
                <!-- LEFT CELL -->
                <td style="width:50%; vertical-align:bottom; padding:0 0 10px 0; font-size:16px; line-height:22px;">
                    ${regards}<br>
                    ${team}<br/><br/>
                    *******************<br/>
                    <a href="mailto:${hostEmail}" style="color:#0000ee; font-size:16px; text-decoration:none;">${hostEmail}</a><br/>
                    <a href="https://www.${omsHostDomain}" target="_blank" style="color:#0000ee; font-size:16px; text-decoration:none;">www.${omsHostDomain}</a>
                    <div style="margin-top:8px;">
                        <#if omsInstaLink?? && omsInstaLink?has_content>
                            <a href="${omsInstaLink}" target="_blank" style="margin-right:5px; display:inline-block;">
                                <img src="${headerLink}/email_logo_insta_01.png" alt="Instagram"
                                     style="width:40px;height:40px;">
                            </a>
                        </#if>
                        <#if omsFbLink?? && omsFbLink?has_content>
                            <a href="${omsFbLink}" target="_blank" style="display:inline-block;">
                                <img src="${headerLink}/email_logo_fb_01.png" alt="Facebook"
                                     style="width:40px;height:40px;">
                            </a>
                        </#if>
                    </div>
                </td>

                <!-- RIGHT CELL -->
                <td style="width:50%; text-align:right; vertical-align:bottom; padding-right:20px;">
                    <a href="https://www.${omsHostDomain}">
                        <img src="${headerLink}/email_logo_round_01.png" alt="${omsHostName} Logo"
                             style="width:150px;height:auto;">
                    </a>
                </td>
            </tr>
        </table>

    </div>
</div>

</body>
</html>
