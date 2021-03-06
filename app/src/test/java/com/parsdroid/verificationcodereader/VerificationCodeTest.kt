package com.parsdroid.verificationcodereader

import com.parsdroid.verificationcodereader.SmsListener.Companion.findVerificationCodeInText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class VerificationCodeTest {

    @Test
    fun `returns null when is empty`() {
        val verifyCode = findVerificationCodeInText("")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is whitespace`() {
        val verifyCode = findVerificationCodeInText(" ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is two whitespace`() {
        val verifyCode = findVerificationCodeInText("  ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is new line`() {
        val verifyCode = findVerificationCodeInText("\n")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is no keyword and code`() {
        val verifyCode = findVerificationCodeInText("asd asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is code but no keyword`() {
        val verifyCode = findVerificationCodeInText("asd asd 4534 asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is keyword but no code`() {
        val verifyCode = findVerificationCodeInText("asd code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when code is before keyword`() {
        val verifyCode = findVerificationCodeInText("asd 45456 code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is no code after keyword with colon`() {
        val verifyCode = findVerificationCodeInText("asd code: asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns code when there is code after keyword`() {
        val inputCode = "45456"
        val verifyCode = findVerificationCodeInText("asd رمز $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code when there is code after word contains keyword`() {
        val inputCode = "45456"
        val verifyCode = findVerificationCodeInText("asd کد تایید: $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code after keyword when there is code after and before keyword`() {
        val firstCode = "12345"
        val secondCode = "56789"
        val verifyCode = findVerificationCodeInText("$firstCode asd کد تایید: $secondCode asd asd as dsa")
        assertEquals(secondCode, verifyCode)
    }

    @Test
    fun `returns first code when there is two code after keyword`() {
        val firstCode = "12345"
        val secondCode = "56789"
        val verifyCode = findVerificationCodeInText("asd کد تایید: $firstCode asd asd $secondCode dsa")
        assertEquals(firstCode, verifyCode)
    }

    @Test
    fun `returns code when there is no space before code`() {
        val inputCode = "45456"
        val verifyCode = findVerificationCodeInText("asd کد تایید:$inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code when is actual sms text`() {
        val verifyCode = findVerificationCodeInText(
            "*پارسیان بانک ایرانیان*\n" +
                    " انتقال به\n" +
                    "622106*7802\n" +
                    "مبلغ  10,000\n" +
                    "رمز  455443"
        )
        assertEquals("455443", verifyCode)
    }
}