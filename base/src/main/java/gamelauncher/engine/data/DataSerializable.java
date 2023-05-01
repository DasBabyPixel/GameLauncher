/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.data;

/**
 * @author DasBabyPixel
 */
public interface DataSerializable {

    /**
     * Writes this object to the buffer
     */
    void write(DataBuffer buffer);

    /**
     * Reads data from the buffer into this object
     */
    void read(DataBuffer buffer);

}
