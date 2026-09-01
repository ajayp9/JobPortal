import requests

SPRING_BOOT_URL = "http://localhost:9090"


class JobService:

    @staticmethod
    def get_all_jobs():

        try:

            response = requests.get(
                f"{SPRING_BOOT_URL}/api/jobs/getall"
            )

            response.raise_for_status()

            return response.json()

        except Exception as e:

            print(e)

            return []