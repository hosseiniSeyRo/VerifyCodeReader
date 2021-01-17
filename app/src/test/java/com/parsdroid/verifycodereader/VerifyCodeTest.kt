package com.parsdroid.verifycodereader

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VerifyCodeTest {

    @Test
    fun `returns null when is empty`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is whitespace`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText(" ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is two whitespace`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("  ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is new line`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("\n")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is there is no keyword`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is there is code but no keyword`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd asd 4534 asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is there is no digit`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is there is keyword but no code`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is there is keyword before code`() {
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd 45456 code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns code when there is code after keyword`() {
        val inputCode = "45456"
        val verifyCode = SmsListener.getVerifyCodeFromSmsText("asd رمز $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code when there is code after word contains keyword`() {
        val inputCode = "45456"
        val verifyCode =
            SmsListener.getVerifyCodeFromSmsText("asd کد تایید: $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }
}