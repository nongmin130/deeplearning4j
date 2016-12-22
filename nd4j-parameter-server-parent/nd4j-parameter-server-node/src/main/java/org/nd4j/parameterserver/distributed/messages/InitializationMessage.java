package org.nd4j.parameterserver.distributed.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.util.FastMath;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.parameterserver.distributed.logic.WordVectorStorage;

/**
 * @author raver119@gmail.com
 */
@NoArgsConstructor
@Builder
@Data
public class InitializationMessage extends BaseVoidMessage {

    protected int vectorLength;
    protected int numWords;
    protected long seed;
    protected boolean useHs;
    protected boolean useNeg;
    protected int columnsPerShard;

    public InitializationMessage(int vectorLength, int numWords, long seed, boolean useHs, boolean useNeg, int columnsPerShard) {
        super(4);
        this.vectorLength = vectorLength;
        this.numWords = numWords;
        this.seed = seed;
        this.useHs = useHs;
        this.useNeg = useNeg;
        this.columnsPerShard = columnsPerShard;
    }

    /**
     * This method initializes shard storage with given data
     */
    @Override
    public void processMessage() {
        // protection check, we definitely don't want double spending here
        INDArray syn0 = storage.getArray(WordVectorStorage.SYN_0);
        INDArray syn1 = storage.getArray(WordVectorStorage.SYN_1);
        INDArray syn1Neg = storage.getArray(WordVectorStorage.SYN_1_NEGATIVE);
        INDArray expTable = storage.getArray(WordVectorStorage.EXP_TABLE);
        if (syn0 == null) {
            // we initialize only syn0/syn1/syn1neg and expTable
            // negTable will be initalized at driver level and will be shared via message
            Nd4j.getRandom().setSeed(seed * (shardIndex + 1));

            int[] shardShape = new int[]{numWords, columnsPerShard};

            syn0 = Nd4j.rand(shardShape, 'c').subi(0.5).divi(vectorLength);

            if (useHs)
                syn1 = Nd4j.create(shardShape, 'c');

            if (useNeg)
                syn1Neg = Nd4j.create(shardShape, 'c');

            // we handle full exp table here
            expTable = initExpTable(100000);


            storage.setArray(WordVectorStorage.SYN_0, syn0);

            if (useHs)
                storage.setArray(WordVectorStorage.SYN_1, syn1);
            if (useNeg)
                storage.setArray(WordVectorStorage.SYN_1_NEGATIVE, syn1Neg);

            storage.setArray(WordVectorStorage.EXP_TABLE, expTable);
        }
    }

    protected INDArray initExpTable(int tableWidth) {
        double[] expTable = new double[tableWidth];
        for (int i = 0; i < expTable.length; i++) {
            double tmp =   FastMath.exp((i / (double) expTable.length * 2 - 1) * 6);
            expTable[i]  = tmp / (tmp + 1.0);
        }

        return Nd4j.create(expTable);
    }
}
