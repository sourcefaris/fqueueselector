/*WAIT TIME REPORT*/
CREATE OR REPLACE VIEW vw_rpt_wait_time AS select `queue`.`process`.`NO` AS `no`,`queue`.`process`.`COUNTER_NO` AS `counter_no`,timediff(`queue`.`process`.`TIME_CALL`,`queue`.`process`.`TIME`) AS `time_diff`,`queue`.`process`.`TIME` AS `time`,`queue`.`process`.`TIME_LAST_CALL` AS `time_call` from `queue`.`process` where ((`queue`.`process`.`TIME_CALL` <> '0000-00-00 00:00:00') and (`queue`.`process`.`SERVICE` in ('qtreg','qtmulti','qtprf','qtprfcsprf','qtregcs','qtprio','qtpriocsprio','qtsrycssry','qtsry')));

CREATE OR REPLACE VIEW vw_rpt_wait_time_cs AS select `queue`.`process`.`NO` AS `no`,`queue`.`process`.`COUNTER_NO` AS `counter_no`,timediff(`queue`.`process`.`TIME_CALL`,`queue`.`process`.`TIME`) AS `time_diff`,`queue`.`process`.`TIME` AS `time`,`queue`.`process`.`TIME_LAST_CALL` AS `time_call` from `queue`.`process` where ((`queue`.`process`.`TIME_CALL` <> '0000-00-00 00:00:00') and (`queue`.`process`.`SERVICE` in ('qcs','qcstreg','qcsprio','qcsprf','qcspriotprio','qcsprftprf','qcssry','qcssrytsry')));

INSERT IGNORE INTO `queue`.`parameter`(`CODE`, `VALUE`, `DESCRIPTION`) VALUES('qcssry', '951-999', 'CS Syariah');

INSERT IGNORE INTO `queue`.`parameter`(`CODE`, `VALUE`, `DESCRIPTION`) VALUES('qtsry', '301-700', 'Teller Syariah');

UPDATE `queue`.`parameter` SET `VALUE`='801-950' WHERE `CODE`='qcs';

UPDATE `queue`.`parameter` SET `DESCRIPTION`='type of branch (set normal / syariah / no video / convenSry)' WHERE `CODE`='displayScr';