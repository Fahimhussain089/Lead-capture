package com.assignment.dynamicform.lead

import com.assignment.dynamicform.designsystem.form.CHECKED_VALUE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeadFormValidatorTest {

    @Test
    fun emptyForm_reportsEveryVisibleRequiredField_andSkipsHiddenCompany() {
        val errors = validateLeadForm(LeadFormConfig.fields, LeadFormConfig.emptyValues())

        assertEquals("Enter full name.", errors[LeadFields.FULL_NAME])
        assertEquals("Enter email.", errors[LeadFields.EMAIL])
        assertEquals("Select lead type.", errors[LeadFields.LEAD_TYPE])
        assertEquals("Enter phone.", errors[LeadFields.PHONE])
        assertEquals("Consent is required.", errors[LeadFields.CONSENT])
        assertFalse(errors.containsKey(LeadFields.COMPANY_NAME))
        assertFalse(errors.containsKey(LeadFields.NOTES))
    }

    @Test
    fun invalidEmailAndShortPhone_useFormatMessages() {
        val errors = validateLeadForm(
            LeadFormConfig.fields,
            validIndividual(
                LeadFields.EMAIL to "ada.example.com",
                LeadFields.PHONE to "12345",
            ),
        )

        assertEquals("Enter a valid email address.", errors[LeadFields.EMAIL])
        assertEquals("Enter a 10-digit number.", errors[LeadFields.PHONE])
    }

    @Test
    fun companyName_isRequiredOnlyWhenLeadTypeIsCompany() {
        val individual = validateLeadForm(
            LeadFormConfig.fields,
            validIndividual(LeadFields.COMPANY_NAME to ""),
        )
        assertFalse(individual.containsKey(LeadFields.COMPANY_NAME))

        val company = validateLeadForm(
            LeadFormConfig.fields,
            validCompany(LeadFields.COMPANY_NAME to "   "),
        )
        assertEquals("Enter company name.", company[LeadFields.COMPANY_NAME])
    }

    @Test
    fun notes_allowEmpty_andRejectMoreThan200Characters() {
        val emptyNotes = validateLeadForm(
            LeadFormConfig.fields,
            validIndividual(LeadFields.NOTES to ""),
        )
        assertTrue(emptyNotes.isEmpty())

        val tooLong = validateLeadForm(
            LeadFormConfig.fields,
            validIndividual(LeadFields.NOTES to "a".repeat(201)),
        )
        assertEquals("Notes must be 200 characters or fewer.", tooLong[LeadFields.NOTES])
    }

    @Test
    fun validIndividualAndCompany_haveNoErrors() {
        assertTrue(validateLeadForm(LeadFormConfig.fields, validIndividual()).isEmpty())
        assertTrue(validateLeadForm(LeadFormConfig.fields, validCompany()).isEmpty())
    }

    @Test
    fun sanitize_keepsDigitsOnPhone_andCapsNotes() {
        val phone = LeadFormConfig.byName.getValue(LeadFields.PHONE)
        val notes = LeadFormConfig.byName.getValue(LeadFields.NOTES)
        val name = LeadFormConfig.byName.getValue(LeadFields.FULL_NAME)

        assertEquals("9876543210", sanitizeLeadValue(phone, "98-765 43210999"))
        assertEquals("a".repeat(200), sanitizeLeadValue(notes, "a".repeat(240)))
        assertEquals("  Ada  ", sanitizeLeadValue(name, "  Ada  "))
    }

    private fun validIndividual(vararg overrides: Pair<String, String>): Map<String, String> {
        val values = LeadFormConfig.emptyValues().toMutableMap()
        values[LeadFields.FULL_NAME] = "Ada Lovelace"
        values[LeadFields.EMAIL] = "ada@example.com"
        values[LeadFields.LEAD_TYPE] = LeadFields.INDIVIDUAL
        values[LeadFields.PHONE] = "9876543210"
        values[LeadFields.NOTES] = "Interested next quarter"
        values[LeadFields.CONSENT] = CHECKED_VALUE
        overrides.forEach { (key, value) -> values[key] = value }
        return values
    }

    private fun validCompany(vararg overrides: Pair<String, String>): Map<String, String> {
        return validIndividual(
            LeadFields.LEAD_TYPE to LeadFields.COMPANY,
            LeadFields.COMPANY_NAME to "Analytical Engines",
            *overrides,
        )
    }
}
