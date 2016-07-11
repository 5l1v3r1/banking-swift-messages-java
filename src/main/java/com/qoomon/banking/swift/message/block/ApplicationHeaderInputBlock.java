package com.qoomon.banking.swift.message.block;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.qoomon.banking.swift.message.block.exception.BlockFieldParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <b>Input Application Header Block</b>
 * <p>
 * <b>Fixed Length Format</b>
 * <pre>
 * 1:  1  - Mode - I = Input, O = Output
 * 2:  3  - Message Type MTxxx e.g. 940
 * 3: 12  - Receiver's address with X in position 9 It is padded with Xs if no branch is required. Typically 8 - BIC, 1 - 'X', 3 - Branch Code
 * 4:  1  - Message Priority - U = Urgent, N = Normal, S = System
 * 5:  1  - Delivery Monitoring - Optional
 * 6:  3  - Obsolescence Period - Optional
 * </pre>
 * <p>
 * <b>Example</b><br>
 * I00BANKDEFFXXXXU3003
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm">https://www.ibm.com/support/knowledgecenter/SSBTEG_4.3.0/com.ibm.wbia_adapters.doc/doc/swift/swift72.htm</a>
 */
public class ApplicationHeaderInputBlock {

    public static final String BLOCK_ID_2 = "2";

    public static final Pattern BLOCK_CONTENT_PATTERN = Pattern.compile("(I)(.{3})(.{12})(.{1})?(.{3})?");

    private final String mode;
    private final String messageType;
    private final String receiverAddress;
    private final String messagePriority;
    private final Optional<String> deliveryMonitoring;
    private final Optional<String> obsolescencePeriod;

    public ApplicationHeaderInputBlock(String mode, String messageType, String receiverAddress, String messagePriority, String deliveryMonitoring, String obsolescencePeriod) {
        this.mode = Preconditions.checkNotNull(mode);
        this.messageType = Preconditions.checkNotNull(messageType);
        this.receiverAddress = Preconditions.checkNotNull(receiverAddress);
        this.messagePriority = Preconditions.checkNotNull(messagePriority);
        this.deliveryMonitoring = Optional.fromNullable(deliveryMonitoring);
        this.obsolescencePeriod = Optional.fromNullable(obsolescencePeriod);
    }

    public static ApplicationHeaderInputBlock of(GeneralBlock block) throws BlockFieldParseException {
        Preconditions.checkArgument(block.getId().equals(BLOCK_ID_2), "unexpected block id '" + block.getId() + "'");

        Matcher blockContentMatcher = BLOCK_CONTENT_PATTERN.matcher(block.getContent());
        if (!blockContentMatcher.matches()) {
            throw new BlockFieldParseException("Block '" + block.getId() + "' content did not match format " + BLOCK_CONTENT_PATTERN);
        }

        String mode = blockContentMatcher.group(1);
        String messageType = blockContentMatcher.group(2);
        String receiverAddress = blockContentMatcher.group(3);
        String messagePriority = blockContentMatcher.group(4);
        String deliveryMonitoring = blockContentMatcher.group(5);
        String obsolescencePeriod = blockContentMatcher.group(6);

        return new ApplicationHeaderInputBlock(mode,messageType,receiverAddress,messagePriority,deliveryMonitoring, obsolescencePeriod);
    }

    public String getMode() {
        return mode;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getMessagePriority() {
        return messagePriority;
    }

    public Optional<String> getDeliveryMonitoring() {
        return deliveryMonitoring;
    }

    public Optional<String> getObsolescencePeriod() {
        return obsolescencePeriod;
    }
}