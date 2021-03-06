package tests;

import dataprovider.DataProvider;
import dataprovider.LoginDataModel;
import groovy.util.logging.Slf4j;
import org.apache.log4j.Logger;
import page.HomePage;
import rule.TestListener;
import base.BaseTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

public class DataDrivenLoginTest extends BaseTest {

    /**
     * CaseNo	Steps	                                        Expected Result
     * Case1	Doğru Kullanıcı adı ve Şifre girilir.	        Login olunduğu görülür
     * Case2	Yanlılş Kullanıcı adı ve Şifre girilir.	        Hatalı E-Posta / Şifre. Tekrar Deneyin." mesajı geldiği görülür
     * Case3	Doğru Kullanıcı ad ve Boş şifre girilir.	    Lütfen şifre giriniz." mesajının geldiği görülür
     * Case4	Boş Kullanıcı adı ve Doğru şifre girilir.	    Lütfen email adresinizi giriniz." mesajının geldiği görülür
     *
     * @param loginData
     * @throws InterruptedException
     */

    @Test(dataProvider = "logindata", dataProviderClass = DataProvider.class)
    public void loginTest(LoginDataModel loginData) {

        BaseTest.caseNo = loginData.getCaseNo();
        new HomePage()
                .closePopUp()
                .clickSignInIconButton()
                .login(loginData.getUsername(), loginData.getPassword())
                .clickLoginButton(loginData.getCaseNo());
    }
}
