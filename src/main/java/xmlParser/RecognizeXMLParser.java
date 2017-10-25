package xmlParser;


import com.pachira.psae.common.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * 识别结果XML解析类
 */

public class RecognizeXMLParser {
    public static boolean parseXML(InputSource in, final List<RecognizeResult> recognizeResults, final boolean drop) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(in, new DefaultHandler() {
                private boolean inSpeech = false;
                private boolean inRecognizeText = false;
                private boolean inRole = false;
                private boolean inEndPoint = false;
                private boolean inItem = false;

                private RecognizeResult recognizeResult;
                private ArrayList<RecogTextWordItem> recogTextWordItems;
                private ArrayList<EndPoint> vadEndPointList;
                private int channel;

                private StringBuilder buffer = new StringBuilder();

                private StringBuilder text = new StringBuilder();
                private StringBuilder time = new StringBuilder();
                private int itemCode = 0;

                @Override
                public void startElement(String uri, String localName,
                                         String qName, Attributes attributes) throws SAXException {
                    if (qName.equals("Speech")) {
                        recognizeResult = new RecognizeResult();
                        recognizeResult.setFilePath(StringUtils.normalizePath(attributes.getValue("Uri")));

                        String duration = attributes.getValue("Duration");
                        recognizeResult.setDuration(
                                StringUtils.isAbsEmpty(duration) ? 0 : Integer.valueOf(duration));

                        inSpeech = true;
                    } else if (inSpeech && qName.equals("Subject")
                            && attributes.getValue("Name").equals("RecognizeText")) {
                        inRecognizeText = true;
                    } else if (inRecognizeText && qName.equals("Role")) {
                        recogTextWordItems = new ArrayList<RecogTextWordItem>();
                        vadEndPointList = new ArrayList<EndPoint>();

                        recognizeResult.getOneBest().put(
                                attributes.getValue(0).equals("R0") ?
                                        RecognizeResult.CHANNEL_N0 : RecognizeResult.CHANNEL_N1,
                                recogTextWordItems);
                        recognizeResult.getVadEndPoint().put(
                                attributes.getValue(0).equals("R0") ?
                                        RecognizeResult.CHANNEL_N0 : RecognizeResult.CHANNEL_N1,
                                vadEndPointList);

                        channel = attributes.getValue(0).equals("R0") ? 0 : 1;

                        inRole = true;
                    } else if (inRole && qName.equals("EndPoint")) {
                        inEndPoint = true;
                    } else if (inEndPoint && qName.equals("Item")) {
                        EndPoint vadEndPoint = new EndPoint();
                        vadEndPoint.setBegin(Integer.valueOf(attributes.getValue("Begin")));
                        vadEndPoint.setEnd(Integer.valueOf(attributes.getValue("End")));
                        vadEndPointList.add(vadEndPoint);

                        inItem = true;
                    }

                    buffer.setLength(0);
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if (qName.equals("Speech")) {
                        recognizeResults.add(recognizeResult);

                        // 片段的错误码
                        if (drop && itemCode != 0) {
                            recognizeResult.setResultCode(itemCode);
                        }

                        inSpeech = false;
                    } else if (qName.equals("ResultCode")) {
                        int resultCode = Integer.valueOf(buffer.toString());

                        // 对于老的VCG，这两个错误码要作为成功处理
                        if (resultCode == 10003 || resultCode == 10018) {
                            resultCode = 0;
                        }

                        if (!inItem) {
                            // 语音的错误码
                            recognizeResult.setResultCode(resultCode);
                        } else {
                            // 如果已经有一段非法了，就保留这段的非法结果
                            // 后面的不要覆盖
                            if (itemCode == 0) {
                                itemCode = resultCode;
                            }
                        }
                    } else if (inSpeech && qName.equals("Subject")) {
                        inRecognizeText = false;
                    } else if (inRecognizeText && qName.equals("Role")) {
                        inRole = false;
                    } else if (inRole && qName.equals("EndPoint")) {
                        inEndPoint = false;
                    } else if (inEndPoint && qName.equals("Item")) {
                        // 将Time和Text关联
                        if (!StringUtils.isAbsEmpty(text) && !StringUtils.isAbsEmpty(time)) {
                            String[] textSplit = text.toString().split("\\s+");
                            String[] timeSplit = time.toString().split("\\s+");
                            if (textSplit.length == timeSplit.length) {
                                for (int i = 0; i < textSplit.length; i++) {
                                    RecogTextWordItem item = new RecogTextWordItem();
                                    item.setWord(textSplit[i]);
                                    item.setBegin(Integer.valueOf(timeSplit[i].split(",")[0]));
                                    item.setEnd(Integer.valueOf(timeSplit[i].split(",")[1]));
                                    item.setChannel(channel);
                                    recogTextWordItems.add(item);
                                }
                            }
                        }

                        text.setLength(0);
                        time.setLength(0);
                        inItem = false;
                    } else if (inItem && qName.equals("Text")) {
                        text.append(buffer);
                    } else if (inItem && qName.equals("Time")) {
                        time.append(buffer);
                    }

                    buffer.setLength(0);
                }

                @Override
                public void characters(char[] ch, int start, int length)
                        throws SAXException {
                    buffer.append(ch, start, length);
                }
            });

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
