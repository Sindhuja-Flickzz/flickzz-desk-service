-- To terminate access to Database
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'flickzz_desk_local'
  AND pid <> pg_backend_pid();

-- Drop Database
DROP DATABASE flickzz_desk_local WITH (FORCE);
