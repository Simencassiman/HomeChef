package com.simencassiman.homechef;

import com.simencassiman.homechef.model.Unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static com.simencassiman.homechef.model.Unit.CL;
import static com.simencassiman.homechef.model.Unit.CUP;
import static com.simencassiman.homechef.model.Unit.DL;
import static com.simencassiman.homechef.model.Unit.G;
import static com.simencassiman.homechef.model.Unit.KG;
import static com.simencassiman.homechef.model.Unit.L;
import static com.simencassiman.homechef.model.Unit.ML;
import static com.simencassiman.homechef.model.Unit.OUNCE;
import static com.simencassiman.homechef.model.Unit.POUND;
import static com.simencassiman.homechef.model.Unit.UNITS;
import static com.simencassiman.homechef.model.Unit.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ModelTests {

    Unit[] units;
    Unit[] weightUnits = new Unit[4];
    Unit[] volumeUnits = new Unit[5];

    @BeforeAll
    public void initializeUnits(){
        units = Unit.values();

        weightUnits[0] = KG;
        weightUnits[1] = G;
        weightUnits[2] = POUND;
        weightUnits[3] = OUNCE;

        volumeUnits[0] = L;
        volumeUnits[1] = DL;
        volumeUnits[2] = CL;
        volumeUnits[3] = ML;
        volumeUnits[4] = CUP;
    }

    //****************
    //     Units
    //****************
    @Test
    public void initializedCorrectly(){
        assertTrue(units.length > 0);
        assertTrue(weightUnits.length > 0);
        assertTrue(volumeUnits.length > 0);
    }

    @Test
    public void unitEqualsItself() {
        for(Unit unit: units)
            assertTrue(unit.equals(unit));
    }

    @Test
    public void unitNotEqualOtherUnit(){
        for(int i = 0; i < units.length; i++){
            for(int j = 0; j < units.length; j++){
                if(j != i){
                    assertFalse(units[i].equals(units[j]));
                }
            }
        }
    }

    @Test
    public void validWeightUnitsCompatible(){
        for (Unit unit : weightUnits) {
            for (Unit value : weightUnits) {
                assertTrue(unit.compatible(value));
            }
        }
    }

    @Test
    public void validVolumeUnitsCompatible(){
        for(Unit unit: volumeUnits){
            for(Unit value: volumeUnits){
                assertTrue(unit.compatible(value));
            }
        }
    }

    @Test
    public void invalidUnitsCompatibleWithOthers(){
        for(Unit unit: weightUnits){
            assertFalse(UNITS.compatible(unit));
        }
        for(Unit unit: volumeUnits){
            assertFalse(UNITS.compatible(unit));
        }
        assertFalse(UNITS.compatible(UNKNOWN));
    }

    @Test
    public void invalidUnknownCompatibleWithOthers(){
        for(Unit unit: weightUnits){
            assertFalse(UNKNOWN.compatible(unit));
        }
        for(Unit unit: volumeUnits){
            assertFalse(UNKNOWN.compatible(unit));
        }
        assertFalse(UNKNOWN.compatible(UNITS));
    }

    @Test
    public void validGetCompatibleWeightUnits(){
        List<Unit> compatibleUnits;
        for(Unit unit: weightUnits){
            compatibleUnits = unit.getCompatibleUnits();
            assertEquals(compatibleUnits.size(), weightUnits.length);
            for(Unit value: weightUnits){
                assertTrue(compatibleUnits.contains(value));
            }
        }
    }

    @Test
    public void validGetCompatibleVolumeUnits(){
        List<Unit> compatibleUnits;
        for(Unit unit: volumeUnits){
            compatibleUnits = unit.getCompatibleUnits();
            assertEquals(compatibleUnits.size(),volumeUnits.length);
            for(Unit value: volumeUnits){
                assertTrue(compatibleUnits.contains(value));
            }
        }
    }

    @Test
    public void validUnitsCompatibleUnits(){
        List<Unit> compatibleUnits = UNITS.getCompatibleUnits();
        assertEquals(1,compatibleUnits.size());
        assertTrue(compatibleUnits.contains(UNITS));
    }

    @Test
    public void validUnknownCompatibleUnits(){
        List<Unit> compatibleUnits = UNKNOWN.getCompatibleUnits();
        assertEquals(1, compatibleUnits.size());
        assertTrue(compatibleUnits.contains(UNKNOWN));
    }

    @Test
    public void validWeightMultiples(){
        assertEquals(0, KG.getMultiples().size());
        assertEquals(1, G.getMultiples().size());
        assertTrue(G.getMultiples().contains(KG));

        assertEquals(0, POUND.getMultiples().size());
        assertEquals(1, OUNCE.getMultiples().size());
        assertTrue(OUNCE.getMultiples().contains(POUND));
    }

    @Test
    public void validVolumeMultiples(){
        assertEquals(0,L.getMultiples().size());

        assertEquals(1,DL.getMultiples().size());
        assertTrue(DL.getMultiples().contains(L));

        assertEquals(2, CL.getMultiples().size());
        assertTrue(CL.getMultiples().contains(L));
        assertTrue(CL.getMultiples().contains(DL));

        assertEquals(3, ML.getMultiples().size());
        assertTrue(ML.getMultiples().contains(L));
        assertTrue(ML.getMultiples().contains(DL));
        assertTrue(ML.getMultiples().contains(CL));

        assertEquals(0, CUP.getMultiples().size());
    }

    @Test
    public void validUnitsMultiples(){
        assertEquals(0, UNITS.getMultiples().size());
    }

    @Test
    public void validUnknownMultiples(){
        assertEquals(0, UNKNOWN.getMultiples().size());
    }

    @Test
    public void validWeightSubmultiples(){
        assertEquals(1, KG.getSubmultiples().size());
        assertTrue(KG.getSubmultiples().contains(G));

        assertEquals(0,G.getSubmultiples().size());

        assertEquals(1, POUND.getSubmultiples().size());
        assertTrue(POUND.getSubmultiples().contains(OUNCE));

        assertEquals(0, OUNCE.getSubmultiples().size());
    }

    @Test
    public void validVolumeSubmultiples(){
        assertEquals(3, L.getSubmultiples().size());
        assertTrue(L.getSubmultiples().contains(DL));
        assertTrue(L.getSubmultiples().contains(CL));
        assertTrue(L.getSubmultiples().contains(ML));

        assertEquals(2, DL.getSubmultiples().size());
        assertTrue(DL.getSubmultiples().contains(CL));
        assertTrue(DL.getSubmultiples().contains(ML));

        assertEquals(1, CL.getSubmultiples().size());
        assertTrue(CL.getSubmultiples().contains(ML));

        assertEquals(0, ML.getSubmultiples().size());

        assertEquals(0, CUP.getSubmultiples().size());
    }

    @Test
    public void validUnitSubmultiples(){
        assertEquals(0, UNITS.getSubmultiples().size());
    }

    @Test
    public void validUnknownSubmultiples(){
        assertEquals(0, UNKNOWN.getSubmultiples().size());
    }

    @Test
    public void validWeightNormalizingFactors(){
        assertEquals(1000, KG.getNormalizationFactor());
        assertEquals(1, G.getNormalizationFactor());
        assertEquals(453.592, POUND.getNormalizationFactor());
        assertEquals(28.35, OUNCE.getNormalizationFactor());
    }

    @Test
    public void validVolumeNormalizingFactors(){
        assertEquals(1000, L.getNormalizationFactor());
        assertEquals(100, DL.getNormalizationFactor());
        assertEquals(10, CL.getNormalizationFactor());
        assertEquals(1, ML.getNormalizationFactor());
        assertEquals(236.59, CUP.getNormalizationFactor());
    }

    @Test
    public void validUnitsNormalizingFactor(){
        assertEquals(1, UNITS.getNormalizationFactor());
    }

    @Test
    public void validUnknownNormalizingFactor(){
        assertEquals(1, UNKNOWN.getNormalizationFactor());
    }

    @Test
    public void validWeightConversions(){
        // kg - kg
        assertEquals(1, KG.getConversionFactor(KG));

        // kg - g
        assertEquals(1000, KG.getConversionFactor(G));
        assertEquals(0.001, G.getConversionFactor(KG));

        // kg - pound
        assertEquals(1000/453.592, KG.getConversionFactor(POUND));
        assertEquals(453.592/1000, POUND.getConversionFactor(KG));

        // kg - ounce
        assertEquals(1000/28.35, KG.getConversionFactor(OUNCE));
        assertEquals(28.35/1000, OUNCE.getConversionFactor(KG));

        // g - g
        assertEquals(1, G.getConversionFactor(G));

        // g - pound
        assertEquals(1/453.592, G.getConversionFactor(POUND));
        assertEquals(453.592, POUND.getConversionFactor(G));

        // g - ounce
        assertEquals(1/28.35, G.getConversionFactor(OUNCE));
        assertEquals(28.35, OUNCE.getConversionFactor(G));

        // pound - pound
        assertEquals(1, POUND.getConversionFactor(POUND));

        // pound - ounce
        assertEquals(453.592/28.35, POUND.getConversionFactor(OUNCE));
        assertEquals(28.35/453.592, OUNCE.getConversionFactor(POUND));

        // ounce - ounce
        assertEquals(1, OUNCE.getConversionFactor(OUNCE));
    }

    @Test
    public void validVolumeConversions(){
        // l - l
        assertEquals(1, L.getConversionFactor(L));

        // l - dl
        assertEquals(10, L.getConversionFactor(DL));
        assertEquals(0.1, DL.getConversionFactor(L));

        // l - cl
        assertEquals(100, L.getConversionFactor(CL));
        assertEquals(0.01, CL.getConversionFactor(L));

        // l - ml
        assertEquals(1000, L.getConversionFactor(ML));
        assertEquals(0.001, ML.getConversionFactor(L));

        // dl - dl
        assertEquals(1, DL.getConversionFactor(DL));

        // dl - cl
        assertEquals(10, DL.getConversionFactor(CL));
        assertEquals(0.1, CL.getConversionFactor(DL));

        // cl - cl
        assertEquals(1, CL.getConversionFactor(CL));
    }

}