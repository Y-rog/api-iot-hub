CREATE TABLE push_subscriptions (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    endpoint TEXT NOT NULL UNIQUE,
                                    p256dh VARCHAR(255) NOT NULL,
                                    auth VARCHAR(255) NOT NULL,
                                    created_at TIMESTAMP NOT NULL DEFAULT now()
);