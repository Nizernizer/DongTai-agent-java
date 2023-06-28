package io.dongtai.iast.openapi.convertor;

import io.dongtai.iast.openapi.domain.DataType;
import io.dongtai.iast.openapi.domain.Schema;

/**
 * 用于把Map类型转为Open API的类型
 *
 * @author CC11001100
 * @since v1.12.0
 */
public class MapOpenApiSchemaConvertor extends BaseOpenApiSchemaConvertor {

    public MapOpenApiSchemaConvertor(OpenApiSchemaConvertorManager manager) {
        super(manager);
    }

    @Override
    public String getConvertorName() {
        return "map-convertor";
    }

    @Override
    public boolean canConvert(Class clazz) {
        return false;
    }

    @Override
    public Schema convert(Class clazz) {
        return new Schema(DataType.Object());
    }
    
}
