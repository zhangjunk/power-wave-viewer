package com.cn.jk.powerwaveviewer.handle;

 import com.cn.jk.powerwaveviewer.service.impl.HCLPartialDischargeDataParserImpl;
 import com.cn.jk.powerwaveviewer.service.impl.UHFPPartialDischargeDataParserImpl;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;

 import javax.annotation.PostConstruct;
 import java.util.HashMap;
 import java.util.Map;

@Component
 public class DataWaveHandlerFactory {
   public static Map<String, DataWaveHandler> handlers = new HashMap<>();

     @Autowired
     private HCLPartialDischargeDataParserImpl hclparserImpl;
    @Autowired
    private UHFPPartialDischargeDataParserImpl uhfpparserImpl;

     @PostConstruct
     public void initHandlers() {
         // 通过实例引用非静态方法
         handlers.put("hcl_prpd", hclparserImpl::prpdWaveData);
         handlers.put("hcl_prps", hclparserImpl::prpsWaveData);
         handlers.put("hcl_sybx", hclparserImpl::sybxWaveData);

         handlers.put("uhfp_prpd", uhfpparserImpl::prpdWaveData);
         handlers.put("uhfp_prps", uhfpparserImpl::prpsWaveData);
         handlers.put("uhfp_sybx", uhfpparserImpl::sybxWaveData);

     }
 }