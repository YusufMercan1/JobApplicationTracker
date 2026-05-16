# ER-diagram — Job Application Tracker

```mermaid
erDiagram
    USER ||--o{ JOB_APPLICATION : owns
    JOB_APPLICATION ||--o{ INTERVIEW : has
    INTERVIEW ||--o{ PARTICIPANT : includes

    USER {
        bigint id PK
        varchar username
        varchar password
        varchar email
    }

    JOB_APPLICATION {
        bigint id PK
        varchar company_name
        varchar role
        varchar status
        date applied_date
        date deadline
        text notes
        boolean reminder_sent
        bigint user_id FK
    }

    INTERVIEW {
        bigint id PK
        datetime date_time
        varchar location
        boolean reminder_sent
        bigint job_application_id FK
    }

    PARTICIPANT {
        bigint id PK
        varchar name
        varchar email
        bigint interview_id FK
    }
```

## Relationer

- En **användare** kan ha många **jobbansökningar**.
- En **jobbansökan** kan ha många **intervjumöten**.
- Ett **intervjumöte** kan ha många **deltagare**.

## Statusvärden (jobbansökan)

- `APPLIED` — Sökt
- `INTERVIEW` — Intervju
- `REJECTED` — Avslagen
