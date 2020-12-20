package com.autarky.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Vishal Bhanot on 25-08-2020,August,2020
 * Company: Autarky Services
 * Support E-mail: vishu.bhanot18@gmail.com
 * Web: https://theautarky.com
 * Phone: +91-78769-22777
 * Skype: vishu1602
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationBaseResponse implements Serializable {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;
}
