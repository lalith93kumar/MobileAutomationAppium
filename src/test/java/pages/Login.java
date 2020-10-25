package pages;

import UIFrame.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.junit.Assert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import stepDefinitions.Hooks;

import javax.swing.plaf.PanelUI;

public class Login extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc=\"Navigate up\"]")
    private WebElement NavigateButton;

    @AndroidFindBy(id = "view_drawer_header_login")
    private WebElement signIn;

    @AndroidFindBy(id = "view_drawer_header_username")
    private WebElement userDetails;

    @AndroidFindBy(id = "login_header_text")
    private WebElement checkingCrediential;

    @AndroidFindBy(id = "login_username")
    private WebElement userName;

    @AndroidFindBy(id = "login_password")
    private WebElement password;

    @AndroidFindBy(id = "login_cancel")
    private WebElement cancelButton;

    @AndroidFindBy(id = "login_login")
    private WebElement submitButton;

    @AndroidFindBy(id = "login_error_label")
    private WebElement errorMessageLable;

    public Login()
    {
        super(new Hooks().server.getDriver());
        PageFactory.initElements(new AppiumFieldDecorator(this.driver),this);
    }

    public void signUp(String usernameData,String passwordData)
    {
        clickOn(NavigateButton);
        clickOn(signIn);
        if(usernameData!=null && usernameData!="")
            sendKeys(userName,usernameData);
        if(passwordData!=null && passwordData!="")
            sendKeys(password,passwordData);
        clickOn(submitButton);
    }

    public void waitForCheckCredentaials()
    {
        waitForElementToBeInvisible(checkingCrediential,10);
    }

    public void verifySignUp(String usernameData)
    {
        clickOn(NavigateButton);
        Assert.assertEquals(getText(userDetails),"Welcome "+usernameData+"!");
    }

    public void verifyNotSignUp()
    {
        clickOn(NavigateButton);
        Assert.assertEquals(isDisplayed(signIn),true);
    }

    public void verifyErrorMessage(String message)
    {
        Assert.assertEquals(getText(errorMessageLable),message);
    }
}
