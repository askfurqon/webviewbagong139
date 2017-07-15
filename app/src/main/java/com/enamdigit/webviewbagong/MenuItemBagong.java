package com.enamdigit.webviewbagong;

class MenuItemBagong {

    private final String mButtonTitle;
    private final String mHtmlToLoad;

    public MenuItemBagong(String ButtonTitle, String HtmlToLoad) {
        this.mButtonTitle = ButtonTitle;
        this.mHtmlToLoad = HtmlToLoad;
    }


    public String getButtonTitle() {
        return mButtonTitle;
    }

    public String getUrltoLoad(){
        return mHtmlToLoad;
    }

}
