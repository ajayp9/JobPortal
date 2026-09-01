using MailKit.Net.Smtp;
using MailKit.Security;
using MimeKit;
using RegistrationEmailService.DTOs;

namespace RegistrationEmailService.Services;

public class EmailService
{
    private readonly IConfiguration configuration;

    public EmailService(IConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public async Task SendRegistrationEmail(
        RegistrationEmailRequest request)
    {
        string role =
            request.Role == "JOB_SEEKER"
                ? "Job Seeker"
                : "Employer";

        string? smtpHost =
            configuration["Smtp:Host"];

        string? smtpPortValue =
            configuration["Smtp:Port"];

        string? smtpEmail =
            configuration["Smtp:Email"];

        string? smtpPassword =
            configuration["Smtp:Password"];

        /*
         * Temporary debugging.
         * Do not print the actual password.
         */
        Console.WriteLine(
            $"SMTP Host: {smtpHost}"
        );

        Console.WriteLine(
            $"SMTP Port: {smtpPortValue}"
        );

        Console.WriteLine(
            $"SMTP Email: {smtpEmail}"
        );

        Console.WriteLine(
            $"SMTP Password Present: " +
            $"{!string.IsNullOrWhiteSpace(smtpPassword)}"
        );

        Console.WriteLine(
            $"SMTP Password Length: " +
            $"{smtpPassword?.Length}"
        );

        if (string.IsNullOrWhiteSpace(smtpHost))
        {
            throw new Exception(
                "SMTP host is missing"
            );
        }

        if (string.IsNullOrWhiteSpace(smtpPortValue))
        {
            throw new Exception(
                "SMTP port is missing"
            );
        }

        if (string.IsNullOrWhiteSpace(smtpEmail))
        {
            throw new Exception(
                "SMTP email is missing"
            );
        }

        if (string.IsNullOrWhiteSpace(smtpPassword))
        {
            throw new Exception(
                "SMTP password is missing"
            );
        }

        int smtpPort =
            int.Parse(smtpPortValue);

        string body = $"""
        <html>
        <body style="
            font-family:Arial;
            background:#f4f4f4;
            padding:30px;
        ">

            <div style="
                max-width:600px;
                margin:auto;
                background:white;
                padding:30px;
                border-radius:10px;
            ">

                <h1 style="color:#111827;">
                    Welcome to NicheNest
                </h1>

                <h2>
                    Hello {request.Name},
                </h2>

                <p>
                    Your registration has been completed successfully.
                </p>

                <p>
                    <strong>Role:</strong> {role}
                </p>

                <p style="color:green;">
                    <strong>Status:</strong>
                    Registration Successful
                </p>

                <a href="http://localhost:5173/login"
                   style="
                       display:inline-block;
                       background:#facc15;
                       color:#111;
                       padding:12px 25px;
                       text-decoration:none;
                       border-radius:6px;
                       margin-top:15px;
                   ">
                    Login Now
                </a>

                <p style="
                    margin-top:30px;
                    color:#666;
                ">
                    Need help? Contact support at
                    {smtpEmail}
                </p>

            </div>

        </body>
        </html>
        """;

        MimeMessage message = new();

        message.From.Add(
            new MailboxAddress(
                "NicheNest",
                smtpEmail
            )
        );

        message.To.Add(
            new MailboxAddress(
                request.Name,
                request.Email
            )
        );

        message.Subject =
            "NicheNest Registration Successful";

        message.Body = new TextPart("html")
        {
            Text = body
        };

        using SmtpClient client = new();

        try
        {
            await client.ConnectAsync(
                smtpHost,
                smtpPort,
                SecureSocketOptions.StartTls
            );

            await client.AuthenticateAsync(
                smtpEmail,
                smtpPassword
            );

            await client.SendAsync(message);

            await client.DisconnectAsync(true);

            Console.WriteLine(
                "Registration email sent successfully to: "
                + request.Email
            );
        }
        catch (Exception exception)
        {
            Console.WriteLine(
                "Registration email failed: "
                + exception.Message
            );

            throw;
        }
    }
}