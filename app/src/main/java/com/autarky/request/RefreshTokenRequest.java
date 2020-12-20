package com.autarky.request;

/**
 * Created by Khushboo Jain on 28-09-2020,September,2020
 * Company:
 * Support E-mail: khushboojain942@gmail.com
 * Web:
 * Phone: +91-78799-77162
 * Skype: khushboojain942
 */


public class RefreshTokenRequest extends ApplicationBaseRequest {

    private String oldToken;

    public String getOldToken() {
        return oldToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }


}
