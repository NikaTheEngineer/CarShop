package io.nikolozp.database;

import io.nikolozp.constants.MyConstants;
import io.nikolozp.database.entities.Company;
import io.nikolozp.database.entities.Model;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.json.JSONObject;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class DatabaseController {

    private final static Logger LOGGER = Logger.getLogger(DatabaseController.class.getName());

    public String sellModel(JSONObject obj) {
        Session session = null;
        String msg = null;
        try {
            logMsg(Level.INFO, "Opening session for selling a model!");
            session = HibernateUtil.getSessionFactory().openSession();
            String sql = "select * from Companies where company_name = :name ;";
            TypedQuery<Company> query = session.createNativeQuery(sql, Company.class);
            logMsg(Level.INFO, "Searching if company exists in the database.");
            query.setParameter("name", obj.getString(MyConstants.COMPANY));
            Company com = query.getSingleResult();
            logMsg(Level.INFO, "Company exists!");
            logMsg(Level.INFO, "Checking if model is in the database!");
            sql = "select * from Models where company_id = :companyId and model_name = :name ;";
            TypedQuery<Model> modelTypedQuery = session.createNativeQuery(sql, Model.class);
            modelTypedQuery.setParameter("companyId", com.getCompany_id());
            modelTypedQuery.setParameter("name", obj.getString(MyConstants.MODEL));
            try {
                Model model = modelTypedQuery.getSingleResult();
                logMsg(Level.INFO, "Model found.");
                if(Integer.parseInt(obj.getString(MyConstants.MONEY)) >= model.getPrice()) {
                    logMsg(Level.INFO, "Enough money, selling the product");
                    Transaction tr = session.beginTransaction();
                    if(model.getCount() == 1) {
                        logMsg(Level.INFO, "Last object, removing from the database.");
                        session.remove(model);
                    }
                    else {
                        logMsg(Level.INFO, "Decreasing count for a given model");
                        model.setCount(model.getCount()-1);
                        session.saveOrUpdate(model);
                    }
                    tr.commit();
                    msg = MyConstants.PRODUCT_SOLD;
                }
                else {
                    logMsg(Level.INFO, "Not enough money.");
                    msg = MyConstants.NOT_ENOUGH_MONEY;
                }
            }
            catch (NoResultException e) {
                logMsg(Level.INFO, "Model does not exists");
                logMsg(Level.INFO, "Model cannot be sold");
                msg = MyConstants.MODEL_NOT_FOUND;
            }
        }
        catch (NoResultException e) {
            logMsg(Level.INFO, "Company was not found in the database");
            logMsg(Level.INFO, "Product does not exist");
            msg = MyConstants.PRODUCT_NOT_FOUND;
        }
        catch (Exception e) {
            logMsg(Level.WARNING, "Unexpected error: \n" + e.getStackTrace().toString());
            msg = MyConstants.UNEXPECTED_ERROR;
        }
        finally {
            logMsg(Level.INFO, "Closing session!");
            closeSession(session);
        }
        logMsg(Level.INFO, "Returning message: " + msg);
        return msg;
    }

    public List<JSONObject> searchModel(JSONObject obj) {
        List<JSONObject> toRet = new ArrayList<>();
        Session session = null;
        try {
            logMsg(Level.INFO, "Opening session for searching a model!");
            session = HibernateUtil.getSessionFactory().openSession();
            String sqlToGetModels = "select * from Models where model_name like :name ;";
            String sqlToGetCompanies = "select * from Companies where company_name like :name ;";
            TypedQuery<Model> modelTypedQuery = session.createNativeQuery(sqlToGetModels, Model.class);
            TypedQuery<Company> companyTypedQuery = session.createNativeQuery(sqlToGetCompanies, Company.class);
            modelTypedQuery.setParameter("name", "%" + obj.getString(MyConstants.MODEL) + "%");
            companyTypedQuery.setParameter("name", "%" + obj.getString(MyConstants.COMPANY) + "%");
            logMsg(Level.INFO,"Searching for company and models");
            List<Model> models = modelTypedQuery.getResultList();
            List<Company> companies = companyTypedQuery.getResultList();
            for (Model model : models) {
                for (Company comp : companies) {
                    if(model.getCompany_id() == comp.getCompany_id()) {
                        addNewJSONToArr(model, comp, toRet);
                    }
                }
            }
        }
        catch (NoResultException e) {
            logMsg(Level.INFO, "No data was found, maybe it never existed or is sold out, who knows...");
        }
        catch (Exception e) {
            e.printStackTrace();
            logMsg(Level.WARNING, "Unexpected error: \n" + e.getStackTrace().toString());
        }
        finally {
            logMsg(Level.INFO, "Closing session!");
            closeSession(session);
        }
        logMsg(Level.INFO, "Returning list, size of the list is: " + toRet.size());
        return toRet;
    }

    private void addNewJSONToArr(Model model, Company comp, List<JSONObject> toRet) {
        JSONObject obj = new JSONObject();
        obj.put(MyConstants.MODEL, model.getModel_name());
        obj.put(MyConstants.COMPANY, comp.getCompany_name());
        obj.put(MyConstants.PRICE, model.getPrice());
        obj.put(MyConstants.COUNT, model.getCount());
        toRet.add(obj);
    }

    public String addNewModel(JSONObject obj) {
        Session session = null;
        String msg = null;
        try {
            logMsg(Level.INFO, "Opening session for adding a new model!");
            session = HibernateUtil.getSessionFactory().openSession();
            String sql = "select * from Companies where company_name = :name ;";
            TypedQuery<Company> query = session.createNativeQuery(sql, Company.class);
            logMsg(Level.INFO, "Searching if company exists in the database.");
            query.setParameter("name", obj.getString(MyConstants.COMPANY));
            Company com = query.getSingleResult();
            logMsg(Level.INFO, "Company exists!");
            logMsg(Level.INFO, "Checking if model is already in the database!");
            sql = "select * from Models where company_id = :companyId and model_name = :name ;";
            TypedQuery<Model> modelTypedQuery = session.createNativeQuery(sql, Model.class);
            modelTypedQuery.setParameter("companyId", com.getCompany_id());
            modelTypedQuery.setParameter("name", obj.getString(MyConstants.MODEL));
            try {
                Model model = modelTypedQuery.getSingleResult();
                logMsg(Level.INFO, "Model exists increasing count.");
                Transaction tr = session.beginTransaction();
                model.setCount(model.getCount()+1);
                session.saveOrUpdate(model);
                tr.commit();
                msg = MyConstants.MODEL_ALREADY_EXISTS;
            }
            catch (NoResultException e) {
                logMsg(Level.INFO, "Model does not exists, creating new one");
                Model toAdd = new Model();
                toAdd.setCount(1);
                toAdd.setPrice(0); // use soap here later
                toAdd.setModel_name(obj.getString(MyConstants.MODEL));
                toAdd.setCompany_id(com.getCompany_id());
                session.save(toAdd);
                logMsg(Level.INFO, "Model added");
                msg = MyConstants.MODEL_ADDED;
            }
        }
        catch (NoResultException e) {
            logMsg(Level.INFO, "Company was not found in the database");
            logMsg(Level.INFO, "Model won't be added!");
            msg = MyConstants.MODEL_NOT_ADDED;
        }
        catch (Exception e) {
            logMsg(Level.WARNING, "Unexpected error: \n" + e.getStackTrace().toString());
            msg = MyConstants.UNEXPECTED_ERROR;
        }
        finally {
            logMsg(Level.INFO, "Closing session!");
            closeSession(session);
        }
        logMsg(Level.INFO, "Returning message: " + msg);
        return msg;
    }

    public String addNewCompany(JSONObject obj) {
        Session session = null;
        String msg = null;
        try {
            logMsg(Level.INFO, "Opening session for adding a new company!");
            session = HibernateUtil.getSessionFactory().openSession();
            String sql = "select * from Companies where company_name = :name ;";
            TypedQuery<Company> query = session.createNativeQuery(sql, Company.class);
            logMsg(Level.INFO, "Searching if company already exists in the database.");
            query.setParameter("name", obj.getString(MyConstants.COMPANY));
            Company com = query.getSingleResult();
            System.out.println(com.getCompany_id() + " " + com.getCompany_name());
            logMsg(Level.INFO, "Company already exists!");
            msg = MyConstants.COMPANY_ALREADY_EXISTS;
        }
        catch (NoResultException e) {
            logMsg(Level.INFO, "Company was not found in the database adding a new one");
            Company toAdd = new Company();
            toAdd.setCompany_name(obj.getString(MyConstants.COMPANY));
            session.save(toAdd);
            logMsg(Level.INFO, "Company added!");
            msg = MyConstants.COMPANY_ADDED;
        }
        catch (Exception e) {
            logMsg(Level.WARNING, "Unexpected error: \n" + e.getStackTrace().toString());
            msg = MyConstants.UNEXPECTED_ERROR;
        }
        finally {
            logMsg(Level.INFO, "Closing session!");
            closeSession(session);
        }
        logMsg(Level.INFO, "Returning message: " + msg);
        return msg;
    }

    private void closeSession(Session session) {
        if(session==null) return;
        session.close();
    }

    private void logMsg(Level level, String msg) {
        FileHandler fh = null;
        try {
            fh = new FileHandler("C:\\Users\\npest\\Desktop\\Server\\MyLogFile.log", true);
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            LOGGER.log(level, msg);
        }
        catch(Exception e) {
        }
        finally {
            if(fh!=null) {
                fh.close();
            }
        }
    }

    public List<JSONObject> searchCompany(JSONObject obj) {
        List<JSONObject> toRet = new ArrayList<>();
        Session session = null;
        try {
            logMsg(Level.INFO, "Opening session for searching a model!");
            session = HibernateUtil.getSessionFactory().openSession();
            String sqlToGetCompanies = "select * from Companies where company_name like :name ;";
            TypedQuery<Company> companyTypedQuery = session.createNativeQuery(sqlToGetCompanies, Company.class);
            companyTypedQuery.setParameter("name", "%" + obj.getString(MyConstants.COMPANY) + "%");
            logMsg(Level.INFO,"Searching for company and models");
            List<Company> companies = companyTypedQuery.getResultList();
            for (Company comp : companies) {
                JSONObject toAdd = new JSONObject();
                toAdd.put(MyConstants.COMPANY, comp.getCompany_name());
                toRet.add(toAdd);
            }
        }
        catch (NoResultException e) {
            logMsg(Level.INFO, "No data was found.");
        }
        catch (Exception e) {
            e.printStackTrace();
            logMsg(Level.WARNING, "Unexpected error: \n" + e.getStackTrace().toString());
        }
        finally {
            logMsg(Level.INFO, "Closing session!");
            closeSession(session);
        }
        logMsg(Level.INFO, "Returning list, size of the list is: " + toRet.size());
        return toRet;
    }
}
