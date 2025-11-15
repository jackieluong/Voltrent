package com.hcmut.voltrent.lib.client;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Consumer;

@Builder
@Getter
@Setter
public class HttpCallbackHandler<T> {
    public Consumer<T> onSuccess;
    public Consumer<Throwable> onError;

}
