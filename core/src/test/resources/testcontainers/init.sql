-- create tables, types and triggers
CREATE TYPE task_status AS ENUM ('NEW', 'RUNNING','COMPLETED','FAILED');
CREATE TABLE tasks(id UUID,label text, 	payload JSONB, status task_status, update_time timestamp);

CREATE OR REPLACE FUNCTION task_status_notify()
    RETURNS trigger AS
'
BEGIN
    PERFORM pg_notify(''coronamq_task_update'', jsonb_build_object(''id'', NEW.id::text, ''payload'',new.payload, ''label'',new.label)::text);
    RETURN NEW;
END;
' LANGUAGE plpgsql;


CREATE TRIGGER task_status_change
    AFTER INSERT
    ON tasks
    FOR EACH ROW
EXECUTE PROCEDURE task_status_notify();