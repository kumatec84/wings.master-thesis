package de.kubbillum.masterthesis.rxcheckwrapper.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ERezept {

  private EVerordnungsdaten verordnungdaten;

  private EQuittungsdaten quittungsdaten;

  private EAbgabedaten abgabedaten;
  
}
