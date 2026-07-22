INSERT INTO alert_rules (id, sensor_type, operator, threshold, message) VALUES
                                                                            (gen_random_uuid()::text, 'co2', 'GREATER_THAN', 1000, 'Niveau de CO2 élevé'),
                                                                            (gen_random_uuid()::text, 'radonShortTermAvg', 'GREATER_THAN', 150, 'Niveau de radon élevé'),
                                                                            (gen_random_uuid()::text, 'voc', 'GREATER_THAN', 250, 'Niveau de COV élevé'),
                                                                            (gen_random_uuid()::text, 'pm25', 'GREATER_THAN', 35, 'Particules fines PM2.5 élevées');