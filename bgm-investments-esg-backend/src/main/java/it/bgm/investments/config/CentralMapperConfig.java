package it.bgm.investments.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Configurazione centrale condivisa per tutti i mapper MapStruct dell'applicazione.
 * <p>
 * Impostazioni principali:
 * <ul>
 *   <li><b>componentModel = "spring"</b>: i mapper che utilizzano questa config
 *   verranno registrati come bean Spring e potranno essere iniettati tramite dependency injection.</li>
 *   <li><b>unmappedTargetPolicy = ReportingPolicy.ERROR</b>: se un campo della
 *   classe di destinazione non viene mappato, MapStruct genererà un errore in fase di compilazione.
 *   Questo aiuta a mantenere i mapping allineati quando si modificano i DTO o le entità.</li>
 * </ul>
 * <p>
 * Esempio di utilizzo:
 * <pre>{@code
 * @Mapper(config = CentralMapperConfig.class)
 * public interface AssetMapper {
 *     // metodi di mapping
 * }
 * }</pre>
 */
@MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CentralMapperConfig {
}