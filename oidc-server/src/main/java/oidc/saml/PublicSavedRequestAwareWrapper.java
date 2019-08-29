package oidc.saml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.security.web.savedrequest.FastHttpDateFormat;
import org.springframework.security.web.savedrequest.SavedRequest;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class PublicSavedRequestAwareWrapper extends HttpServletRequestWrapper {
    //~ Static fields/initializers =====================================================================================

    protected static final Log logger = LogFactory.getLog(PublicSavedRequestAwareWrapper.class);
    protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

    /** The default Locale if none are specified. */
    protected static Locale defaultLocale = Locale.getDefault();

    //~ Instance fields ================================================================================================

    protected SavedRequest savedRequest = null;

    /**
     * The set of SimpleDateFormat formats to use in getDateHeader(). Notice that because SimpleDateFormat is
     * not thread-safe, we can't declare formats[] as a static variable.
     */
    protected final SimpleDateFormat[] formats = new SimpleDateFormat[3];

    //~ Constructors ===================================================================================================

    public PublicSavedRequestAwareWrapper(SavedRequest saved, HttpServletRequest request) {
        super(request);
        savedRequest = saved;

        formats[0] = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        formats[1] = new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US);
        formats[2] = new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US);

        formats[0].setTimeZone(GMT_ZONE);
        formats[1].setTimeZone(GMT_ZONE);
        formats[2].setTimeZone(GMT_ZONE);
    }

    //~ Methods ========================================================================================================

    @Override
    public Cookie[] getCookies() {
        List<Cookie> cookies = savedRequest.getCookies();

        return cookies.toArray(new Cookie[cookies.size()]);
    }

    @Override
    public long getDateHeader(String name) {
        String value = getHeader(name);

        if (value == null) {
            return -1L;
        }

        // Attempt to convert the date header in a variety of formats
        long result = FastHttpDateFormat.parseDate(value, formats);

        if (result != -1L) {
            return result;
        }

        throw new IllegalArgumentException(value);
    }

    @Override
    public String getHeader(String name) {
        List<String> values = savedRequest.getHeaderValues(name);

        return values.isEmpty() ? null : values.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getHeaderNames() {
        return new Enumerator<String>(savedRequest.getHeaderNames());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getHeaders(String name) {
        return new Enumerator<String>(savedRequest.getHeaderValues(name));
    }

    @Override
    public int getIntHeader(String name) {
        String value = getHeader(name);

        if (value == null) {
            return -1;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Locale getLocale() {
        List<Locale> locales = savedRequest.getLocales();

        return locales.isEmpty() ? Locale.getDefault() : locales.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getLocales() {
        List<Locale> locales = savedRequest.getLocales();

        if (locales.isEmpty()) {
            // Fall back to default locale
            locales = new ArrayList<Locale>(1);
            locales.add(Locale.getDefault());
        }

        return new Enumerator<Locale>(locales);
    }

    @Override
    public String getMethod() {
        return savedRequest.getMethod();
    }

    /**
     * If the parameter is available from the wrapped request then the request has been forwarded/included to a URL
     * with parameters, either supplementing or overriding the saved request values.
     * <p>
     * In this case, the value from the wrapped request should be used.
     * <p>
     * If the value from the wrapped request is null, an attempt will be made to retrieve the parameter
     * from the saved request.
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);

        if (value != null) {
            return value;
        }

        String[] values = savedRequest.getParameterValues(name);

        if (values == null || values.length == 0) {
            return null;
        }

        return values[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        Set<String> names = getCombinedParameterNames();
        Map<String, String[]> parameterMap = new HashMap<String, String[]>(names.size());

        for (String name : names) {
            parameterMap.put(name, getParameterValues(name));
        }

        return parameterMap;
    }

    @SuppressWarnings("unchecked")
    private Set<String> getCombinedParameterNames() {
        Set<String> names = new HashSet<String>();
        names.addAll(super.getParameterMap().keySet());
        names.addAll(savedRequest.getParameterMap().keySet());

        return names;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        return new Enumerator(getCombinedParameterNames());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] savedRequestParams = savedRequest.getParameterValues(name);
        String[] wrappedRequestParams = super.getParameterValues(name);

        if (savedRequestParams == null) {
            return wrappedRequestParams;
        }

        if (wrappedRequestParams == null) {
            return savedRequestParams;
        }

        // We have parameters in both saved and wrapped requests so have to merge them
        List<String> wrappedParamsList = Arrays.asList(wrappedRequestParams);
        List<String> combinedParams = new ArrayList<String>(wrappedParamsList);

        // We want to add all parameters of the saved request *apart from* duplicates of those already added
        for (String savedRequestParam : savedRequestParams) {
            if (!wrappedParamsList.contains(savedRequestParam)) {
                combinedParams.add(savedRequestParam);
            }
        }

        return combinedParams.toArray(new String[combinedParams.size()]);
    }

}
