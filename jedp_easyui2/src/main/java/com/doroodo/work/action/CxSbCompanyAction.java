package com.doroodo.work.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.doroodo.base.action.BaseAction;
import com.doroodo.config.SysVal;
import com.doroodo.base.util.excelTools.Excel;
import com.doroodo.base.util.excelTools.Table;

import com.doroodo.base.model.*;
import com.doroodo.sys.model.*;
import com.doroodo.sys.service.*;

import com.doroodo.work.model.*;
import com.doroodo.work.service.*;

@Controller
@ParentPackage(value="sys")   
@InterceptorRef("mydefault")
public class CxSbCompanyAction extends BaseAction{
	@Autowired
	private CxSbCompanyService cxSbCompanyService;
	private CxSbCompany cxSbCompany;
	private String EXCEL_TITLE = "";//请输入导出的excel表表名
	private String tableHtml="";
	private String tableTitle="";
	public String getTableHtml() {
		return tableHtml;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}
	
	public CxSbCompany getCxSbCompany(){
		return cxSbCompany;
	}
	
	public void setCxSbCompany(CxSbCompany cxSbCompany){
		this.cxSbCompany=cxSbCompany;
	}
	
	@Action("/sys/cxSbCompany_Add")
	public void cxSbCompanyAdd(){
		cxSbCompanyService.saveOrUpdate(cxSbCompany);
		if(cxSbCompany.getId()!=null){
			Map m=new HashMap();
			m.put("info", SysVal.ADD_SUC);
			m.put("flowId", cxSbCompany.getId());
			this.writeJson(m);
		}else{
			writeMsg(SysVal.ADD_ER);
		}
	}
	
	@Action("/sys/cxSbCompany_Add_HasFiles")
	public void cxSbCompanyAddHasFiles(){
		cxSbCompanyService.saveOrUpdate(cxSbCompany);
		Map m=new HashMap();
		if(cxSbCompany.getId()!=null){
			m.put("info", SysVal.ADD_SUC);
			m.put("flowId", cxSbCompany.getId());
			m.put("fileid", "cxSbCompany-"+cxSbCompany.getId());
			this.writeJson(m);
		}else{
			m.put("info", SysVal.ADD_ER);
			this.writeJson(m);
		}
	}
	
	@Action("/sys/cxSbCompany_Delete_HasFiles")
	public void cxSbCompanyDeleteHasFiles(){
		if(this.getIds().trim()=="")return;
		cxSbCompanyService.delete(this.getIds());
		Map m=new HashMap();
		m.put("info", SysVal.DEL_SUC);
		String[] ids=this.getIds().split(",");
		String fileids="";
		for(int i=0;i<ids.length;i++){
			String fileid=ids[i];
			if(!fileid.isEmpty()){
				fileids+= "cxSbCompany-"+fileid+",";
			}
		}
		m.put("fileids", fileids);
		this.writeJson(m);
	}
	
	@Action("/sys/cxSbCompany_List")
	public void cxSbCompanyList(){
		if(cxSbCompany!=null){
			this.writeJson(cxSbCompanyService.dataGrid(this.getPage(), this.getRows(),cxSbCompany));
		}else{
			this.writeJson(cxSbCompanyService.dataGrid(this.getPage(), this.getRows(), this.getSearchName(), this.getSearchKey()));
		}
	}
	
	@Action("/sys/cxSbCompany_List_All")
	public void cxSbCompanyListAll(){
		this.writeJson(cxSbCompanyService.listAll());
	}
	
	@Action("/sys/cxSbCompany_ComboBox")
	public void cxSbCompanyComboBox(){
		List<CxSbCompany> l = cxSbCompanyService.listAll();
		List<comboBox> l_=new ArrayList<comboBox>();
		for(int i=0;i<l.size();i++){
			comboBox cb = new comboBox();
			CxSbCompany obj=l.get(i);
			cb.setId(obj.getId().toString());
			cb.setText(obj.getName());//请按需修改!
			l_.add(cb);
		}
		this.writeJson(l_);
	}
	
	@Action("/sys/cxSbCompany_Delete")
	public void cxSbCompanyDelete(){
		if(this.getIds().trim()=="")return;
		cxSbCompanyService.delete(this.getIds());
		writeMsg(SysVal.DEL_SUC);
	}
	
	@Action("/sys/cxSbCompany_Update")
	public void cxSbCompanyUpdate(){
		if(cxSbCompany!=null) cxSbCompanyService.saveOrUpdate(cxSbCompany);
		writeMsg(SysVal.EDIT_SUC);
	}
	
	@Action("/sys/cxSbCompany_Excel")
	public void cxSbCompanyExcel(){
		List<Object> list = new ArrayList();
		if(this.getExcelExportAll().equalsIgnoreCase("false")){
			list = cxSbCompanyService.dataGrid(this.getPage(), this.getRows(), this.getSearchName(), this.getSearchKey()).getRows();//获取数据
		}else if(this.getExcelExportAll().equalsIgnoreCase("true")){
			List l = cxSbCompanyService.listAll();//获取数据
			list=l;
		}
		super.excel(EXCEL_TITLE,list);
	}
	
	@Action("/sys/cxSbCompany_FormFile")
	public void cxSbCompanyFormFile(){
		getSession().put("tableHtml", tableHtml); 
		getSession().put("tableTitle", tableTitle); 
	}
	
	@Action("/sys/cxSbCompany_Upload")
	public void cxSbCompanyUpload() throws IOException{
		String msg=SysVal.UPDATA_SUC;
		List<File> fileGroup=this.getFileGroup();
		List<String> fileGroupFileName=this.getFileGroupFileName();
		if(fileGroup==null||fileGroupFileName==null){this.writeMsg(SysVal.NOFILE); return;}
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//设置日期格式
		try {
			Map< String, String> map=this.uploadFiles(fileGroup,fileGroupFileName,"");
			for(int i=0;i<fileGroupFileName.size();i++){
				SyFile syFile_=new SyFile();
				String fileName=fileGroupFileName.get(i);
				String sysName=map.get(fileName);
				String createTime=df.format(new Date());
				syFile_.setCreatetime(createTime);
				syFile_.setFilename(fileName);
				syFile_.setSysname(sysName);
				syFile_.setUserid(this.getLoginUserId());
				syFileService.saveOrUpdate(syFile_);
				Excel e = new Excel(fileName,new FileInputStream(fileGroup.get(i)));
				Table t=e.getSheet(0).getAsTable();
				String[] ps=t.getHeader();
				cxSbCompany=new CxSbCompany();
				 for(int ei=0;ei<t.getRowSize();ei++){//读出行
					 for(int ej=0;ej<t.getColSize();ej++){//读出列
						 setPValue(cxSbCompany,ps[ej],t.getCell(ei,ej));
					 }
					 cxSbCompany.setId(null);
					 cxSbCompanyService.saveOrUpdate(cxSbCompany);
				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg=SysVal.UPDATA_ER;
		}finally{
			writeMsg(msg);
		}
	}
	
	private void setPValue(CxSbCompany cxSbCompany,String p,String v){
		Field field = null;
		try {
			field = cxSbCompany.getClass().getDeclaredField(p);
			field.setAccessible(true);  
			if("java.lang.integer".equalsIgnoreCase(field.getType().getName())){
				field.set(cxSbCompany,Float.valueOf(v).intValue());
			}else{
				field.set(cxSbCompany,v);  
			}
		} catch (Exception e) {
			System.out.println("++++++++++++++++++++您需要转下类型，doroodo平台缺少您说的这个类型，烦请加下，或者联系下维护人员!++++++++++++++++++++");
			e.printStackTrace();
		}
	}
	
	@Action("/sys/cxSbCompany_Get_ById")  
	public void cxSbCompanyGetById(){
		CxSbCompany cxSbCompany=new CxSbCompany();
		cxSbCompany.setId(Integer.parseInt(this.getId()));
		cxSbCompany=cxSbCompanyService.get(cxSbCompany).get(0);
		if(cxSbCompany==null){
			this.writeMsg(SysVal.GET_ER);
			return;
		}else{
			this.writeJson(cxSbCompany);
		}
	}
	
	@Action("/sys/cxSbCompany_Get_ByObj")  
	public void cxSbCompanyByObj(){
		this.writeJson(cxSbCompanyService.get(cxSbCompany));
	}
	
	//检查字段是否唯一
	private String isSingle(CxSbCompany cxSbCompany,String fieldName,String fieldValue){
		String result=null;
		List<CxSbCompany> lsList = cxSbCompanyService.get(cxSbCompany);
		if(cxSbCompanyService.get(cxSbCompany).size()>0) {
			result=fieldName+"["+fieldValue+"]"+SysVal.READHASE;
			return result;
		}
		return result;
	}

}