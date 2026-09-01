package com.backend.automation;

import com.backend.entities.Job;
import com.backend.entities.User;
import com.backend.repository.JobRepository;
import com.backend.repository.UserRepository;
import com.backend.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewsletterScheduler {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NewsletterScheduler(
            JobRepository jobRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /*
     * Runs every one minute.
     *
     * Spring cron format:
     * second minute hour day month day-of-week
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void sendJobNewsletters() {

        System.out.println("Running Cron Automation");

        List<Job> jobs =
                jobRepository.findByNewslettersSentFalse();

        for (Job job : jobs) {

            try {

                List<User> users =
                        userRepository.findUsersByJobNiche(
                                job.getJobNiche()
                        );

                for (User user : users) {

                    String subject =
                            "Hot Job Alert: "
                                    + job.getTitle()
                                    + " in "
                                    + job.getJobNiche()
                                    + " Available Now";

                    String message =
                            "Hi " + user.getName() + ",\n\n"
                                    + "Great news! A new job that fits your niche "
                                    + "has just been posted.\n\n"
                                    + "The position is for a "
                                    + job.getTitle()
                                    + " with "
                                    + job.getCompanyName()
                                    + ", and they are looking to hire immediately."
                                    + "\n\nJob Details:\n"
                                    + "- Position: "
                                    + job.getTitle()
                                    + "\n"
                                    + "- Company: "
                                    + job.getCompanyName()
                                    + "\n"
                                    + "- Location: "
                                    + job.getLocation()
                                    + "\n"
                                    + "- Salary: "
                                    + job.getSalary()
                                    + "\n\n"
                                    + "Don't wait too long! Job openings like these "
                                    + "are filled quickly.\n\n"
                                    + "We're here to support you in your job search. "
                                    + "Best of luck!\n\n"
                                    + "Best Regards,\n"
                                    + "NicheNest Team";

                    emailService.sendEmail(
                            user.getEmail(),
                            subject,
                            message
                    );
                }

                job.setNewslettersSent(true);
                jobRepository.save(job);

                System.out.println(
                        "Newsletter completed for job ID: "
                                + job.getId()
                );

            } catch (Exception exception) {

                System.err.println(
                        "Error while sending newsletter for job ID: "
                                + job.getId()
                );

                exception.printStackTrace();
            }
        }
    }
}