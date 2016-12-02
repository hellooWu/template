package cn.com.lenovo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;


/**
 * 
 * @author 巫佳龙
 *
 *
 *需要优化的地方:
 *1.优化代码,把初始化的值写进方法中,值写为变量或者读取xml
 *
 *
 */
@Controller
public class TemplateController {
	

	protected final transient Log log = LogFactory.getLog(getClass());
	// private VctlUserService vctlUserService;
	private static VelocityEngine ve;

	static {
		ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		ve.setProperty(Velocity.INPUT_ENCODING, "utf-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "utf-8");   
		try {
			ve.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/saveUeditor")
	public ModelAndView saveUeditor(String title, String filename,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content=request.getParameter("content");
		log.debug("saveUeditor"+content);
		//写文件
		Long time = System.currentTimeMillis();
		String fname = (StringUtils.isEmpty(filename) ? time : filename) + ".html";
		String localPath = request.getSession().getServletContext().getRealPath("resources");
		String uri = "/"+ getNowDateStr("/");
		File dir = new File(localPath + uri);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		writeFile(dir.getPath() + "/" + fname, false, content);

		Map<String, String> page = new HashMap<String, String>();
		page.put("filename", "");
		page.put("title", "");
		page.put("tpl", "ueditor");
		//插入所有模板
		String path = this.getClass().getResource("/template/pages").getPath();
		String[] allTemplate = getAllTemplate(path);		


		Model model = new ExtendedModelMap();
		model.addAttribute("allTemplate", allTemplate);
		model.addAttribute("url", "http://112.74.165.23/template/resources" + uri + "/" + fname);
		model.addAttribute("page", page);
		return new ModelAndView("ueditor", model.asMap());
	}
	
	
	
	
	@RequestMapping(value = "/ueditor")
	public ModelAndView ueditor() throws Exception {
		Map<String, String> page = new HashMap<String, String>();
		page.put("title", "");
		page.put("filename", "");
		page.put("tpl", "ueditor");

		String path = this.getClass().getResource("/template/pages").getPath();
		String[] allTemplate = getAllTemplate(path);

		Model model = new ExtendedModelMap();
		model.addAttribute("page", page);
		model.addAttribute("allTemplate", allTemplate);
		return new ModelAndView("ueditor", model.asMap());
	}
	
	

	@RequestMapping(value = "/newpage")
	public ModelAndView newpage(@RequestParam(required = false, value = "tpl") String tpl) throws Exception {
		log.info("newpage:tpl : " + tpl);
		if (isEmpty(tpl))
			tpl = "picWord"; // 默认多图页面
		
		
		
		Map<String, String> page = new HashMap<String, String>();
		page.put("title", "");
		page.put("filename", "");
		page.put("tpl", tpl);


		String path = this.getClass().getResource("/template/pages").getPath();
		String[] allTemplate = getAllTemplate(path);

		Model model = new ExtendedModelMap();
		model.addAttribute("page", page);
		model.addAttribute("allTemplate", allTemplate);
		return new ModelAndView("newpage", model.asMap());
	}

	// 获得当前文件夹下一层的所有文件名
	private String[] getAllTemplate(String path) {
		File file = new File(path);
		return file.list();
	}

/*	 递归调用获得文件夹下所有的文件(去除后缀)
	private void getAllFileName(String path, ArrayList<String> fileName) {
		File file = new File(path);
		File[] files = file.listFiles();
		for (File a : files) {
			if (a.isDirectory()) {
				getAllFileName(a.getAbsolutePath(), fileName);
			} else {
				fileName.add(a.getName().substring(0, a.getName().lastIndexOf(".")));
			}
		}
	}
*/
	//添加component
	@RequestMapping(value="/addComponent",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String addComponent(@RequestParam(required = true, value = "tpl") String tpl,@RequestParam(required = true, value = "size") String size, 
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		log.info("add : tpl : " + tpl);
		int length=Integer.parseInt(size);
		Template t = ve.getTemplate("/template/adder/cmp.vm");
		VelocityContext ctx = new VelocityContext();
		Map<String, Map<String,String>> componentValMap = new HashMap<>();
		if ("imgs".equals(tpl)) {
			putDefaultValue(componentValMap, "img-link",length,response);
		}else if ("article".equals(tpl)) {
			putDefaultValue(componentValMap, "text-link",length,response);
		}else if ("picWord".equals(tpl)){
			putDefaultValue(componentValMap, "img-link",length,response);
			putDefaultValue(componentValMap, "text-link",length+1,response);
		}
		ctx.put("component-vals", componentValMap);

		StringWriter sw = new StringWriter();

		t.merge(ctx, sw);
		
		return sw.toString();
	}
	
	

	// iframe模板界面
	@RequestMapping(value="/show",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String show(@RequestParam(required = true, value = "tpl") String tpl, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("show : tpl : " + tpl);
		
		Template t = ve.getTemplate("/template/pages/" + tpl + "/show.vm");
		VelocityContext ctx = new VelocityContext();

		ctx.put("title", "模板编辑展示页");
		// add edit control script
		ctx.put("scripts","<script type=\"text/javascript\" src=\"/template/scripts/lib/jquery-1.8.2.min.js\"></script>"
				+"<script type=\"text/javascript\" src=\"/template/scripts/tpl/ifm.tpl.js\" charset=\"utf-8\"></script>"
				+"<script type=\"text/javascript\" src=\"/template/scripts/lib/plugins/jquery.cookie.js\"></script> ");
		ctx.put("styles","<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/template/styles/lib/bootstrap-2.2.1.min.css\" />");
		ctx.put("hidden-input", "<input type=\"hidden\" id=\"currentTemplate\" value=\"" + tpl + "\">");
		// set value
		Map<String, Map<String,String>> componentValMap = getComponentValueFromCookie(request);
		String lack=getLackComponent(componentValMap);//检查是否只有单一种类的component
		// append default values
		if (MapUtils.isEmpty(componentValMap)) {
			if ("imgs".equals(tpl)) {
				putDefaultValue(componentValMap, "img-link",response);
			} else if ("article".equals(tpl)) {
				putDefaultValue(componentValMap, "text-link",response);
			}else if ("picWord".equals(tpl)){
				putDefaultValue(componentValMap, "img-link",response);
				putDefaultValue(componentValMap, "text-link",response);


			}
		}
		//防止只有一个component
		else if("picWord".equals(tpl)){			
			log.info("进来缺少component判断了");
			log.info("lcak:"+lack);
			if("text".equals(lack)){
				log.info("进来缺少text判断了");
				putDefaultValue(componentValMap, "text-link",response);

			}else if("img".equals(lack)){
				log.info("进来缺少img判断了");
				putDefaultValue(componentValMap, "img-link",response);

			}
		}
		else if("imgs".equals(tpl)&&"img".equals(lack)){
			putDefaultValue(componentValMap, "img-link",response);

		}
		else if("article".equals(tpl)&&"text".equals(lack)){
			putDefaultValue(componentValMap, "text-link",response);
		}
		
		log.debug(componentValMap.entrySet().iterator().next().getKey());
		ctx.put("component-vals", componentValMap);

		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);

		return sw.toString();
	}

	@RequestMapping(value="/input",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String input(@RequestParam(required = true, value = "cmpType") String cmpType,
			@RequestParam(required = true, value = "cmpName") String cmpName,
			@RequestParam(required = false, value = "currentTemplate") String currentTemplate,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("input : cmpType : " + cmpType);
		log.info("input : cmpName : " + cmpName);
		log.info("input : currentTemplate : " + currentTemplate);

		String tpl = currentTemplate;
		Template t = ve.getTemplate("/template/components/" + cmpType + "/edit.vm");
		VelocityContext ctx = new VelocityContext();

		ctx.put("cmpType", cmpType);
		ctx.put("cmpName", cmpName);
		
		Map<String, Map<String,String>> componentValMap = getComponentValueFromCookie(request);
		Map<String,String> data=componentValMap.get(cmpName);
		// append default values
		if (MapUtils.isEmpty(data)) {
			if ("imgs".equals(tpl)) {
				data=new HashMap<>();
				data.put("picUrl","http://saas.lenovo.net/a/2016/0906/2009/90b6d48a_4a6629d9850a29_src.jpg");
				data.put("link", "/");
															// 默认的JavaScript:void(0);
															// 显示不全,URLDecoder.decode不会解析
															// ( 左括号?
			} 
			else if ("article".equals(tpl)) {
				data=new HashMap<>();
				data.put("textTitle", "title");
				data.put("textContent", "content");
			}
			else if ("picWord".equals(tpl)){
				data=new HashMap<>();
				data.put("textTitle", "title");
				data.put("textContent", "content");
				data.put("picUrl","http://saas.lenovo.net/a/2016/0906/2009/90b6d48a_4a6629d9850a29_src.jpg");
				data.put("link", "/");
			}
		}
			
		ctx.put("data", data);
		
		StringWriter sw = new StringWriter();
		t.merge(ctx, sw);
		return sw.toString();
	}

	@RequestMapping("/save")
	public ModelAndView save(String tpl, String title, String filename,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		log.info("save : tpl : " + tpl);

		Map<String, Map<String, String>> componentValMap = getComponentValueFromCookie(request);
		
		Template t = ve.getTemplate("template/pages/" + tpl + "/show.vm");
		VelocityContext ctx = new VelocityContext();
		if (MapUtils.isEmpty(componentValMap)) {
			if ("imgs".equals(tpl)) {
				putDefaultValue(componentValMap, "img-link",response);
			}else if ("article".equals(tpl)) {
				putDefaultValue(componentValMap, "text-link",response);
			}else if ("picWord".equals(tpl)){
				putDefaultValue(componentValMap, "img-link",response);
				putDefaultValue(componentValMap, "text-link",response);
			}
		}
		ctx.put("hidden-input", "");
		ctx.put("title", title);
		ctx.put("scripts", "");
		ctx.put("styles", "");
		ctx.put("component-vals", componentValMap);

		StringWriter sw = new StringWriter();

		t.merge(ctx, sw);

		log.info("result : " + sw.toString());
		//写文件
		Long time = System.currentTimeMillis();
		String fname = (StringUtils.isEmpty(filename) ? time : filename) + ".html";
		String localPath = request.getSession().getServletContext().getRealPath("resources");
		String uri = "/"+ getNowDateStr("/");
		File dir = new File(localPath + uri);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		writeFile(dir.getPath() + "/" + fname, false, sw.toString());

		Map<String, String> page = new HashMap<String, String>();
		page.put("filename", filename);
		page.put("title", title);
		page.put("tpl", tpl);
		//插入所有模板
		String path = this.getClass().getResource("/template/pages").getPath();
		String[] allTemplate = getAllTemplate(path);		


		Model model = new ExtendedModelMap();
		model.addAttribute("allTemplate", allTemplate);
		model.addAttribute("url", "http://112.74.165.23/template/resources" + uri + "/" + fname);
		model.addAttribute("page", page);
		return new ModelAndView("newpage", model.asMap());
	}


	
	//遍历componentVals中的key,防止在图片与文字并存的iframe中只有一种component显示
	//因为只有图片和文字,所以最多缺一种,只返回缺少的类型,不缺少返回null
	private String getLackComponent(Map<String, Map<String,String>> componentValMap){
		boolean a = false,b = false;//a代表imgs,b代表text
		for(String s:componentValMap.keySet()){
			String[] temp=s.split("-");
			for(String ss:temp){
				System.out.println(ss);
			}
			if("img".equals(temp[0])){
				a=true;
			}else if("text".equals(temp[0])){
				b=true;
			}
		}
		if(a==true&&b==false){
			return "text";
		}else if(a==false&&b==true){
			return "img";
		}else
			return null;
	}
	
	
	// 解析request中的cookie,获取component-vals
	private Map<String, Map<String,String>> getComponentValueFromCookie(HttpServletRequest request) {
		String componentVals = null;
		Cookie[] cookies = request.getCookies();
		try {
			for (Cookie c : cookies) {
				if (c.getName().equals("component-vals")) {
					componentVals = URLDecoder.decode(c.getValue(), "utf-8");
					break;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		// System.out.println("componentVals:"+componentVals);
		log.info("cookie, component-vals : " + componentVals);
		Map<String, Map<String,String>> componentValMap = new HashMap<>();
		if (null != componentVals && !componentVals.equals("")) {
			componentValMap = fromJson(componentVals);
		}

		return componentValMap;
	}

	// 上传图片controller,OMG你是怎么成功的?!
	@RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
	@ResponseBody
	public String uploadImg(@RequestParam(value = "filename") MultipartFile img, HttpServletRequest request,
			HttpServletResponse response) {
		JSONObject result = new JSONObject();
		String imgPath = null;
		try {
			imgPath = upload(img, request);
			log.info("imgPath:" + imgPath);
		} catch (Exception e) {
			log.debug("上传图片失败");
			e.printStackTrace();
		}
		result.put("url", imgPath);

		response.setContentType("text/html;charset=UTF-8");
		// //解决跨域名访问问题
		// response.setHeader("Access-Control-Allow-Origin", "*");

		return result.toString();
	}

	public String upload(MultipartFile file, HttpServletRequest request) throws Exception {
		// 过滤合法的文件类型
		String fileName = file.getOriginalFilename();
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);// 文件后缀
		String allowSuffixs = "gif,jpg,jpeg,bmp,png,ico";
		if (allowSuffixs.indexOf(suffix) == -1) {
			// params.put("resultStr", "not support the file type!");
			return null;
		}

		String urlPath = "/template/images";
		String localPath = request.getSession().getServletContext().getRealPath("images");

		// 创建新目录,按日期分类(年月日)
		String uri = "/" + getNowDateStr("/");
		File dir = new File(localPath + uri);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 创建新文件
		String newFileName = getUniqueFileName();
		File f = new File(dir.getPath() + "/" + newFileName + "." + suffix);

		// 将输入流中的数据复制到新文件
		FileUtils.copyInputStreamToFile(file.getInputStream(), f);
		String finalImgPath = urlPath + uri.replace("\\", "/") + "/" + newFileName + "." + suffix;
		// params.put("resultStr",finalImgPath);

		return finalImgPath;
	}

	// 返回文件日期作为地址的一部分
	public String getNowDateStr(String separator) {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1;
		int day = now.get(Calendar.DATE);

		return year + separator + month + separator + day;
	}

	// 生成唯一的文件名
	public static String getUniqueFileName() {
		String str = UUID.randomUUID().toString();
		return str.replace("-", "");
	}

	// 重写工具类
	private boolean isEmpty(String s) {
		if (("").equals(s) || s == null) {
			return true;
		}
		return false;
	}

	private Map<String, Map<String,String>> fromJson(String componentVals) {
		LinkedHashMap<String, Map<String,String>> map = JSONObject.parseObject(componentVals,new TypeReference<LinkedHashMap<String, Map<String,String>>>() {
        });
		
		return map;
	}

	private void writeFile(String filename, boolean b, String stringWriter) {
		FileOutputStream fileoutputstream;
		try {
			fileoutputstream = new FileOutputStream(filename);
			byte[] html_bytes = stringWriter.getBytes();
			fileoutputstream.write(html_bytes);
			fileoutputstream.close();
		} catch (IOException e) {
			log.debug("写文件错误");
			e.printStackTrace();
		}

	}
	
	//初始化各类component值的方法
	private void putDefaultValue(Map<String, Map<String, String>> componentValMap,String editorType,HttpServletResponse response){
		HashMap<String,String> data=new HashMap<>();
		if("img-link".equals(editorType)){
			data.put("picUrl","http://saas.lenovo.net/a/2016/0906/2009/90b6d48a_4a6629d9850a29_src.jpg");
			data.put("link", "/");
		}
		else if("text-link".equals(editorType)){
			data.put("textTitle", "title");
			data.put("textContent", "content");
		}		
		componentValMap.put(editorType, data);
	}
	
	//重载,方便命名
	private void putDefaultValue(Map<String, Map<String, String>> componentValMap,String editorType,int size,HttpServletResponse response){
		HashMap<String,String> data=new HashMap<>();
		if("img-link".equals(editorType)){
			data.put("picUrl","http://saas.lenovo.net/a/2016/0906/2009/90b6d48a_4a6629d9850a29_src.jpg");
			data.put("link", "/");
		}
		else if("text-link".equals(editorType)){
			data.put("textTitle", "title");
			data.put("textContent", "content");
		}		
		componentValMap.put(editorType+ "-" + size, data);
	}


}
