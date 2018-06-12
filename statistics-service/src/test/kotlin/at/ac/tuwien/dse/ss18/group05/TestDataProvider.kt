package at.ac.tuwien.dse.ss18.group05

import at.ac.tuwien.dse.ss18.group05.dto.AccidentReport
import at.ac.tuwien.dse.ss18.group05.dto.GpsLocation
import at.ac.tuwien.dse.ss18.group05.dto.MetaData

/**
 * <h4>About this class</h4>

 * <p>Description of this class</p>
 *
 * @author David Molnar
 * @version 1.0.0
 * @since 1.0.0
 */
class TestDataProvider {

    companion object {
        fun testAccidentReport1(): AccidentReport {
            return AccidentReport(
                null,
                "QgYZY8ntPurzGDhxxAcVYbYb",
                MetaData("JH4DB8590SS001561", "1995 Acura Integra"),
                GpsLocation(48.172450, 16.376432),
                4,
                123456,
                123456,
                654321
            )
        }

        fun testAccidentReport2(): AccidentReport {
            return AccidentReport(
                null,
                "QgYZY8ntPurdfDhxxAcVYbYb",
                MetaData("3GCPCSE03BG366866", "Audi TT"),
                GpsLocation(0.0, 0.0),
                1,
                545245,
                545245,
                75425
            )
        }
    }
}