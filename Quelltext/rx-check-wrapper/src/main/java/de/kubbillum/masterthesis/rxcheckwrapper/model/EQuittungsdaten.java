package de.kubbillum.masterthesis.rxcheckwrapper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EQuittungsdaten {

	// q_datum
	private String qDatum;
	
	public String getQDatum () {
		   if (qDatum == null) {
		        qDatum = "00000000";
		    } else if (qDatum.length() > 8) {
		        // Trim the string to 8 characters
		        qDatum = qDatum.substring(0, 8);
		        // Replace the last 2 characters with '00'
		        qDatum = qDatum.substring(0, 6) + "00";
		    }
		   
			// Get the last two characters
			String lastTwoChars = qDatum.substring(6);

			// Check if the last two characters are strictly greater than 31
			if (Integer.parseInt(lastTwoChars) > 31) {
			// Replace the last 2 characters with '00'
				qDatum = qDatum.substring(0, 6) + "00";
			}
	    return qDatum;
	}
}
