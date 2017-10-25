package xmlParser;

import java.util.*;

// 描述一个语音的分析结果
// 对应一个<instance>节点
// 目前需要的就是语音的1best结果
public class RecognizeResult {
    public static final String CHANNEL_N0   = "n0";
    public static final String CHANNEL_N1   = "n1";
    public static final String CHANNEL_MIX  = "mix";

    public static final String MONO         = "mono";
    public static final String STEREO       = "stereo";

    private int resultCode  = -1;
    private String filePath = "";
    private String format   = "";
    private int sampleRate  = 8000;
    private int bitRate     = 16;
    private String channel  = "mono";
    private int duration    = 0;
    private Map<String, List<RecogTextWordItem>> oneBest = new HashMap<String, List<RecogTextWordItem>>();
    private Map<String, List<EndPoint>> vadEndPoint = new HashMap<String, List<EndPoint>>();
    private Map<String, List<EndPoint>> silencePoint = new HashMap<String, List<EndPoint>>();

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Map<String, List<RecogTextWordItem>> getOneBest() {
        return oneBest;
    }

    public void setOneBest(Map<String, List<RecogTextWordItem>> oneBest) {
        this.oneBest = oneBest;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Map<String, List<EndPoint>> getVadEndPoint() {
        return vadEndPoint;
    }

    public void setVadEndPoint(Map<String, List<EndPoint>> vadEndPoint) {
        this.vadEndPoint = vadEndPoint;
    }

    public Map<String, List<EndPoint>> getSilencePoint() {
        return silencePoint;
    }

    public void setSilencePoint(Map<String, List<EndPoint>> silencePoint) {
        this.silencePoint = silencePoint;
    }

    public String toXML() {
        StringBuffer buffer = new StringBuffer("<RecognizeResult>");
        buffer.append("<Speech Uri=\"" + filePath + "\" Duration=\"" + duration + "\">");
        buffer.append("<ResultCode>" + resultCode + "</ResultCode>");

        buffer.append("<Subject Name=\"RecognizeText\">");
        for (String channel : vadEndPoint.keySet()) {
            String role;
            if (channel.equals(CHANNEL_N0)) {
                role = "R0";
            } else {
                role = "R1";
            }
            buffer.append("<Role Name=\"" + role + "\">");

            List<EndPoint> endPointList = vadEndPoint.get(channel);
            Collections.sort(endPointList, new Comparator<EndPoint>() {

                public int compare(EndPoint endPoint1, EndPoint endPoint2) {
                    return endPoint1.getBegin() - endPoint2.getBegin();
                }
            });

            List<RecogTextWordItem> wordItemList = oneBest.get(channel);
            Collections.sort(wordItemList, new Comparator<RecogTextWordItem>() {

                public int compare(RecogTextWordItem word1, RecogTextWordItem word2) {
                    if (word1.getBegin() == word2.getBegin()) {
                        return word1.getEnd() - word2.getEnd();
                    }
                    return word1.getBegin() - word2.getBegin();
                }
            });

            buffer.append("<EndPoint Count=\"" + endPointList.size() + "\">");
            int index = 0;
            for (EndPoint endPoint : endPointList) {
                buffer.append("<Item Begin=\"" + endPoint.getBegin());
                buffer.append("\" End=\"" + endPoint.getEnd() + "\">");

                StringBuilder text = new StringBuilder();
                StringBuilder time = new StringBuilder();
                buffer.append("<Text>");
                while (wordItemList.size() > index) {
                    RecogTextWordItem wordItem = wordItemList.get(index);
                    if (wordItem.getBegin() >= endPoint.getBegin()
                            && wordItem.getEnd() <= endPoint.getEnd()) {
                        text.append(wordItem.getWord() + " ");
                        time.append(wordItem.getBegin() + "," + wordItem.getEnd() + " ");
                        index++;
                    } else {
                        break;
                    }
                }
                buffer.append(text.toString().trim() + "</Text>");
                buffer.append("<Time>" + time.toString().trim() + "</Time>");
                buffer.append("</Item>");

                text = null;
                time = null;
            }

            buffer.append("</EndPoint>");
            buffer.append("</Role>");
        }
        buffer.append("</Subject>");

        buffer.append("<Subject Name=\"LongSilence\">");
        for (String channel : silencePoint.keySet()) {
            String role;
            if (channel.equals(CHANNEL_N0)) {
                role = "R0";
            } else if(channel.equals(CHANNEL_N1)){
                role = "R1";
            } else {
                role = "Mix";
            }

            List<EndPoint> silencePointList = silencePoint.get(channel);
            if(silencePointList == null) {
                continue;
            }

            Collections.sort(silencePointList, new Comparator<EndPoint>() {

                public int compare(EndPoint endPoint1, EndPoint endPoint2) {
                    return endPoint1.getBegin() - endPoint2.getBegin();
                }
            });

            buffer.append("<Role Name=\"" + role + "\">");
            buffer.append("<EndPoint Count=\"" + silencePointList.size() + "\">");
            for (EndPoint endPoint : silencePointList) {
                buffer.append("<Item Begin=\"" + endPoint.getBegin());
                buffer.append("\" End=\"" + endPoint.getEnd() + "\">");
                buffer.append("</Item>");
            }

            buffer.append("</EndPoint>");
            buffer.append("</Role>");
        }
        buffer.append("</Subject>");

        buffer.append("</Speech>");
        buffer.append("</RecognizeResult>");
        return buffer.toString();
    }
}
