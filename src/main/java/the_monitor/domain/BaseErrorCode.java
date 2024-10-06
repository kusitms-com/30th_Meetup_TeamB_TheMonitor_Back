package the_monitor.domain;

import the_monitor.application.ErrorReasonDto;

public interface BaseErrorCode {

    public ErrorReasonDto getReason();

    public ErrorReasonDto getReasonHttpStatus();

}