package com.specmate.emfrest.internal;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.PerThread;
import org.osgi.service.log.LogService;

import com.specmate.common.SpecmateException;
import com.specmate.persistency.IChangeListener;
import com.specmate.persistency.IPersistencyService;
import com.specmate.persistency.ITransaction;
import com.specmate.persistency.IValidator;

public class TransactionFactory implements Factory<ITransaction> {

	private IPersistencyService persistencyService;
	private LogService logService;

	public TransactionFactory(IPersistencyService persistencyService, LogService logService) {
		this.persistencyService = persistencyService;
		this.logService = logService;
	}

	@Override
	public void dispose(ITransaction transaction) {
		transaction.close();
	}

	@PerThread
	@Override
	public ITransaction provide() {
		try {
			logService.log(LogService.LOG_DEBUG, "Create new transaction.");
			List<IChangeListener> validators = new ArrayList<>();
			validators.add(persistencyService.getValidator(IValidator.Type.ID));
			validators.add(persistencyService.getValidator(IValidator.Type.NAME));
			validators.add(persistencyService.getValidator(IValidator.Type.TOPLEVELFOLDER));
			return persistencyService.openTransaction(validators);
		} catch (SpecmateException e) {
			logService.log(LogService.LOG_ERROR, "Transaction factory could not create new transaction", e);
			return null;
		}

	}
}