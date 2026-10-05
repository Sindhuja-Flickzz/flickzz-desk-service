drop table fd_ritm_attachment;
drop table fd_ritm_watchlist;
drop table fd_ritm_audit_detail;
drop table fd_ritm_audit;
drop table fd_ritm_comment;
drop table fd_ritm_field_value;
drop table fd_ritm_approver;
drop table fd_ritm_master;

drop sequence fd_ritm_attachment_seq;
drop sequence fd_ritm_watchlist_seq;
drop sequence fd_ritm_audit_detail_seq;
drop sequence fd_ritm_audit_seq;
drop sequence fd_ritm_comment_seq;
drop sequence fd_ritm_field_value_seq;
drop sequence fd_ritm_approver_seq;
drop sequence fd_ritm_seq;

ALTER TABLE FD_WORK_ITEMS
ADD COLUMN COMPANY_ID BIGINT;

ALTER TABLE FD_WORK_ITEMS
ADD CONSTRAINT FK_WORK_ITEMS_COMPANY FOREIGN KEY (COMPANY_ID)
    REFERENCES FD_COMPANY_MASTER (COMPANY_ID);

update fd_work_items set company_id = 2 where company_id is null;

ALTER TABLE FD_WORK_ITEMS
    ALTER COLUMN COMPANY_ID SET NOT NULL;