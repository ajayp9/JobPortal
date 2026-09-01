using Microsoft.AspNetCore.Mvc;
using RegistrationEmailService.DTOs;
using RegistrationEmailService.Services;

namespace RegistrationEmailService.Controllers;

[ApiController]
[Route("api/registration-email")]
public class RegistrationEmailController : ControllerBase
{
    private readonly EmailService emailService;

    public RegistrationEmailController(
        EmailService emailService)
    {
        this.emailService = emailService;
    }

    [HttpPost("send")]
    public async Task<IActionResult> SendEmail(
        [FromBody] RegistrationEmailRequest request)
    {
        await emailService.SendRegistrationEmail(
            request
        );

        return Ok(
            new
            {
                message =
                    "Registration email sent successfully"
            }
        );
    }
}