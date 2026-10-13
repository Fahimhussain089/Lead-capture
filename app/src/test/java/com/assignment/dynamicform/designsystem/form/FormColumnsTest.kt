package com.assignment.dynamicform.designsystem.form

import androidx.compose.ui.unit.dp
import com.assignment.dynamicform.lead.LeadFields
import com.assignment.dynamicform.lead.LeadFormConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FormColumnsTest {

    @Test
    fun breakpoint_isOneColumnBelow600_andTwoColumnsAt600() {
        assertFalse(isExpandedWidth(390.dp))
        assertFalse(isExpandedWidth(599.dp))
        assertTrue(isExpandedWidth(600.dp))
        assertTrue(isExpandedWidth(914.dp))
    }

    @Test
    fun expandedRows_pairNameAndEmail_andStretchNotesAndConsent() {
        val individual = fieldRows(specs(LeadFormConfig.emptyValues()))
        assertEquals(
            listOf(
                listOf(LeadFields.FULL_NAME, LeadFields.EMAIL),
                listOf(LeadFields.LEAD_TYPE),
                listOf(LeadFields.PHONE),
                listOf(LeadFields.NOTES),
                listOf(LeadFields.CONSENT),
            ),
            individual.map { row -> row.map { it.name } },
        )

        val companyValues = LeadFormConfig.emptyValues() +
            (LeadFields.LEAD_TYPE to LeadFields.COMPANY)
        val company = fieldRows(specs(companyValues))
        assertEquals(
            listOf(
                listOf(LeadFields.FULL_NAME, LeadFields.EMAIL),
                listOf(LeadFields.LEAD_TYPE),
                listOf(LeadFields.COMPANY_NAME),
                listOf(LeadFields.PHONE),
                listOf(LeadFields.NOTES),
                listOf(LeadFields.CONSENT),
            ),
            company.map { row -> row.map { it.name } },
        )
    }

    private fun specs(values: Map<String, String>) =
        LeadFormConfig.visibleFields(values).map { it.spec }
}
