package de.qaware.openapigeneratorforspring.common.reference.component.header;

import de.qaware.openapigeneratorforspring.common.mapper.HeaderAnnotationMapper.HeaderWithOptionalName;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferencedItemConsumerForType;
import de.qaware.openapigeneratorforspring.model.header.Header;

import java.util.List;
import java.util.Map;

public interface ReferencedHeadersConsumer extends ReferencedItemConsumerForType<List<HeaderWithOptionalName>, Map<String, Header>> {

}