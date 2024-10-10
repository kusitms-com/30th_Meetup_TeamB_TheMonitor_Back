package the_monitor.infrastructure.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenStatus {

    AUTHENTICATED,
    EXPIRED,
    INVALID

}