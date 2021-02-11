package com.parsdroid.verifycodereader

import com.parsdroid.verifycodereader.SmsListener.Companion.getVerifyCodeFromSmsText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class VerifyCodeTest {

    @Test
    fun `returns null when is empty`() {
        val verifyCode = getVerifyCodeFromSmsText("")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is whitespace`() {
        val verifyCode = getVerifyCodeFromSmsText(" ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is two whitespace`() {
        val verifyCode = getVerifyCodeFromSmsText("  ")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when is new line`() {
        val verifyCode = getVerifyCodeFromSmsText("\n")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is no keyword and code`() {
        val verifyCode = getVerifyCodeFromSmsText("asd asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is code but no keyword`() {
        val verifyCode = getVerifyCodeFromSmsText("asd asd 4534 asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is no digit`() {
        val verifyCode = getVerifyCodeFromSmsText("asd asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when there is keyword but no code`() {
        val verifyCode = getVerifyCodeFromSmsText("asd code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns null when code is before keyword`() {
        val verifyCode = getVerifyCodeFromSmsText("asd 45456 code asd asd as dsa")
        assertNull(verifyCode)
    }

    @Test
    fun `returns code when there is code after keyword`() {
        val inputCode = "45456"
        val verifyCode = getVerifyCodeFromSmsText("asd رمز $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code when there is code after word contains keyword`() {
        val inputCode = "45456"
        val verifyCode = getVerifyCodeFromSmsText("asd کد تایید: $inputCode asd asd as dsa")
        assertEquals(inputCode, verifyCode)
    }

    @Test
    fun `returns code after keyword when there is code after and before keyword`() {
        val firstCode = "12345"
        val secondCode = "56789"
        val verifyCode = getVerifyCodeFromSmsText("$firstCode asd کد تایید: $secondCode asd asd as dsa")
        assertEquals(secondCode, verifyCode)
    }

    @Test
    fun `returns first code when there is two code after keyword`() {
        val firstCode = "12345"
        val secondCode = "56789"
        val verifyCode = getVerifyCodeFromSmsText("asd کد تایید: $firstCode asd asd $secondCode dsa")
        assertEquals(firstCode, verifyCode)
    }
}
